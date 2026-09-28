package com.locklock.applock.domain.model

import android.graphics.drawable.Drawable

enum class LockMode(val displayName: String) {
    PIN("Mã PIN 4 số"),
    PATTERN("Mẫu hình (Pattern)")
}

enum class RelockPolicy(val displayName: String, val timeoutMs: Long) {
    IMMEDIATE("Ngay sau khi rời ứng dụng", 1_200L),
    AFTER_1_MIN("Sau 1 phút rời ứng dụng", 60_000L),
    AFTER_5_MIN("Sau 5 phút rời ứng dụng", 300_000L),
    ON_SCREEN_OFF("Khi tắt màn hình thiết bị", -1L)
}

enum class DisguiseIconMode(val displayName: String, val aliasClassName: String) {
    DEFAULT("Biểu tượng LockLock gốc", "com.locklock.applock.MainActivity"),
    CALCULATOR("Máy tính (Calculator)", "com.locklock.applock.CalculatorAlias"),
    WEATHER("Thời tiết (Weather)", "com.locklock.applock.WeatherAlias")
}

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val categoryLabel: String,
    val isSystemApp: Boolean,
    val isRecommended: Boolean,
    val isLocked: Boolean,
    val iconDrawable: Drawable? = null
)

data class VaultMediaItem(
    val id: Long,
    val originalName: String,
    val encryptedFileName: String,
    val mediaType: String, // IMAGE or VIDEO
    val fileSizeBytes: Long,
    val addedAt: Long
)

data class IntruderLogItem(
    val id: Long,
    val targetPackage: String,
    val targetAppName: String,
    val photoPath: String?,
    val failedAttempts: Int,
    val timestamp: Long
)
