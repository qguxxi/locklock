package com.locklock.applock.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import com.locklock.applock.data.local.datastore.SecurityPreferences
import com.locklock.applock.data.repository.AppLockRepository
import com.locklock.applock.domain.model.RelockPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UnlockSessionManager — Bộ máy trạng thái chống lặp màn hình khóa (Anti-Trigger Loop State Machine).
 *
 * Khắc phục triệt để lỗi "màn hình khóa hiện lên 6-7 lần liên tiếp" khi một ứng dụng chuyển đổi
 * giữa các Activity nội bộ (Splash -> Ad -> Main) hoặc bật bàn phím / hộp thoại hệ thống.
 */
@Singleton
class UnlockSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppLockRepository,
    private val securityPreferences: SecurityPreferences
) {
    // Lưu thời điểm lần cuối người dùng đang ở trong ứng dụng đã mở khóa
    private val unlockedAppsLastSeen = ConcurrentHashMap<String, Long>()

    @Volatile
    private var currentForegroundPackage: String? = null

    @Volatile
    private var currentlyVerifyingPackage: String? = null

    private val ignoredSystemPackages by lazy {
        buildSet {
            add(context.packageName)
            add("com.android.systemui")
            add("com.google.android.permissioncontroller")
            add("com.android.permissioncontroller")
            add("com.android.packageinstaller")
            add("android")
            // Thêm toàn bộ gói bàn phím (IME) để khi bật bàn phím không làm mất phiên mở khóa
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.enabledInputMethodList?.forEach { add(it.packageName) }
        }
    }

    /**
     * Kiểm tra xem sự kiện mở [packageName] có cần hiển thị màn hình khóa hay không.
     */
    suspend fun shouldLockPackage(packageName: String): Boolean {
        if (packageName.isBlank() || ignoredSystemPackages.contains(packageName)) {
            return false
        }

        val settings = securityPreferences.getSnapshot()
        if (!settings.isMasterProtectionEnabled) {
            return false
        }

        val now = System.currentTimeMillis()
        val previousPkg = currentForegroundPackage

        // Nếu vừa rời khỏi một ứng dụng đã mở khóa sang một ứng dụng khác (hoặc Launcher),
        // cập nhật dấu thời gian rời đi để tính thời gian ân hạn (Grace Period / Relock Timeout)
        if (previousPkg != null && previousPkg != packageName && unlockedAppsLastSeen.containsKey(previousPkg)) {
            unlockedAppsLastSeen[previousPkg] = now
        }

        currentForegroundPackage = packageName

        // Nếu đang hiển thị màn hình khóa cho chính ứng dụng này rồi thì không kích hoạt chồng lên nữa
        if (currentlyVerifyingPackage == packageName) {
            return false
        }

        // Kiểm tra xem ứng dụng này có nằm trong danh sách bị khóa không
        val isLockedTarget = repository.isPackageLocked(packageName)
        if (!isLockedTarget) {
            return false
        }

        // Kiểm tra phiên đã mở khóa và chính sách khóa lại (Relock Policy)
        val lastSeenTime = unlockedAppsLastSeen[packageName]
        if (lastSeenTime != null) {
            val policy = settings.relockPolicy
            val isStillUnlocked = when (policy) {
                RelockPolicy.ON_SCREEN_OFF -> true
                else -> (now - lastSeenTime) <= policy.timeoutMs
            }

            if (isStillUnlocked) {
                // Gia hạn thời gian đang hoạt động trong ứng dụng
                unlockedAppsLastSeen[packageName] = now
                return false
            } else {
                unlockedAppsLastSeen.remove(packageName)
            }
        }

        return true
    }

    fun markVerifying(packageName: String?) {
        currentlyVerifyingPackage = packageName
    }

    fun onPackageUnlocked(packageName: String) {
        unlockedAppsLastSeen[packageName] = System.currentTimeMillis()
        currentlyVerifyingPackage = null
    }

    fun onScreenTurnedOff() {
        unlockedAppsLastSeen.clear()
        currentlyVerifyingPackage = null
        currentForegroundPackage = null
    }

    fun isLauncherPackage(packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }
}
