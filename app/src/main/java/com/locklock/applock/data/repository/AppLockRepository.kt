package com.locklock.applock.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import com.locklock.applock.data.local.db.dao.IntruderLogDao
import com.locklock.applock.data.local.db.dao.LockedAppDao
import com.locklock.applock.data.local.db.dao.VaultMediaDao
import com.locklock.applock.data.local.db.entity.IntruderLogEntity
import com.locklock.applock.data.local.db.entity.LockedAppEntity
import com.locklock.applock.data.local.db.entity.VaultMediaEntity
import com.locklock.applock.domain.model.InstalledAppInfo
import com.locklock.applock.domain.model.IntruderLogItem
import com.locklock.applock.domain.model.VaultMediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.experimental.xor

@Singleton
class AppLockRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lockedAppDao: LockedAppDao,
    private val vaultMediaDao: VaultMediaDao,
    private val intruderLogDao: IntruderLogDao
) {
    private val recommendedPackages = setOf(
        "com.whatsapp",
        "com.facebook.katana",
        "com.facebook.orca",
        "com.instagram.android",
        "com.google.android.gm",
        "com.google.android.apps.photos",
        "com.sec.android.gallery3d",
        "com.android.settings",
        "com.android.vending",
        "org.telegram.messenger",
        "com.zing.zalo"
    )

    fun observeLockedPackageSet(): Flow<Set<String>> =
        lockedAppDao.observeLockedApps().map { list -> list.map { it.packageName }.toSet() }

    suspend fun isPackageLocked(packageName: String): Boolean =
        lockedAppDao.isPackageLocked(packageName)

    suspend fun getInstalledApps(lockedSet: Set<String>): List<InstalledAppInfo> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
            val myPackage = context.packageName

            resolveInfos
                .mapNotNull { resolveInfo ->
                    val pkgName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                    if (pkgName == myPackage) return@mapNotNull null
                    val appInfo = resolveInfo.activityInfo.applicationInfo
                    val label = resolveInfo.loadLabel(pm).toString()
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val isRec = recommendedPackages.contains(pkgName)
                    val categoryLabel = when {
                        isRec -> "Khuyên dùng bảo vệ"
                        isSystem -> "Ứng dụng hệ thống"
                        else -> "Ứng dụng đã cài đặt"
                    }
                    InstalledAppInfo(
                        packageName = pkgName,
                        appName = label,
                        categoryLabel = categoryLabel,
                        isSystemApp = isSystem,
                        isRecommended = isRec,
                        isLocked = lockedSet.contains(pkgName),
                        iconDrawable = runCatching { resolveInfo.loadIcon(pm) }.getOrNull()
                    )
                }
                .distinctBy { it.packageName }
                .sortedWith(
                    compareByDescending<InstalledAppInfo> { it.isLocked }
                        .thenByDescending { it.isRecommended }
                        .thenBy { it.appName.lowercase() }
                )
        }

    suspend fun toggleAppLock(packageName: String, appName: String, shouldLock: Boolean) {
        if (shouldLock) {
            lockedAppDao.upsertLockedApp(
                LockedAppEntity(
                    packageName = packageName,
                    appName = appName,
                    isLocked = true
                )
            )
        } else {
            lockedAppDao.unlockPackage(packageName)
        }
    }

    suspend fun getAppNameForPackage(packageName: String): String {
        return runCatching {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        }.getOrDefault(packageName)
    }

    // --- Privacy Vault (Encrypted Media Storage) ---
    fun observeVaultItems(): Flow<List<VaultMediaItem>> =
        vaultMediaDao.observeVaultMedia().map { entities ->
            entities.map {
                VaultMediaItem(
                    id = it.id,
                    originalName = it.originalName,
                    encryptedFileName = it.encryptedFileName,
                    mediaType = it.mediaType,
                    fileSizeBytes = it.fileSizeBytes,
                    addedAt = it.addedAt
                )
            }
        }

    suspend fun importUriToVault(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri) ?: "image/jpeg"
            val mediaType = if (mimeType.startsWith("video")) "VIDEO" else "IMAGE"

            var displayName = "Vault_${System.currentTimeMillis()}"
            var sizeBytes = 0L
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIdx >= 0) displayName = cursor.getString(nameIdx) ?: displayName
                    if (sizeIdx >= 0) sizeBytes = cursor.getLong(sizeIdx)
                }
            }

            val vaultDir = File(context.filesDir, "serene_vault").apply { mkdirs() }
            val encryptedFileName = "${UUID.randomUUID()}.svault"
            val destFile = File(vaultDir, encryptedFileName)

            resolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var isFirstChunk = true
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        if (isFirstChunk) {
                            // XOR cipher on first 512 bytes header so standard media scanners cannot read it
                            val headerLen = minOf(bytesRead, 512)
                            for (i in 0 until headerLen) {
                                buffer[i] = buffer[i] xor 0x5A.toByte()
                            }
                            isFirstChunk = false
                        }
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }

            if (sizeBytes <= 0L) sizeBytes = destFile.length()
            vaultMediaDao.insertVaultMedia(
                VaultMediaEntity(
                    originalName = displayName,
                    encryptedFileName = encryptedFileName,
                    mediaType = mediaType,
                    fileSizeBytes = sizeBytes
                )
            )
            true
        }.getOrDefault(false)
    }

    suspend fun removeVaultItem(item: VaultMediaItem) = withContext(Dispatchers.IO) {
        val vaultDir = File(context.filesDir, "serene_vault")
        File(vaultDir, item.encryptedFileName).delete()
        vaultMediaDao.deleteVaultMediaById(item.id)
    }

    // --- Intruder Logs ---
    fun observeIntruderLogs(): Flow<List<IntruderLogItem>> =
        intruderLogDao.observeIntruderLogs().map { list ->
            list.map {
                IntruderLogItem(
                    id = it.id,
                    targetPackage = it.targetPackage,
                    targetAppName = it.targetAppName,
                    photoPath = it.photoPath,
                    failedAttempts = it.failedAttempts,
                    timestamp = it.timestamp
                )
            }
        }

    suspend fun recordIntruderAttempt(targetPackage: String, failedAttempts: Int) {
        val appName = getAppNameForPackage(targetPackage)
        intruderLogDao.insertIntruderLog(
            IntruderLogEntity(
                targetPackage = targetPackage,
                targetAppName = appName,
                failedAttempts = failedAttempts
            )
        )
    }

    suspend fun clearIntruderLogs() {
        intruderLogDao.clearAllLogs()
    }
}
