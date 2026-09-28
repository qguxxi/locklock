package com.locklock.applock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.locklock.applock.core.permission.PermissionHelper
import com.locklock.applock.service.overlay.LockOverlayManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AppLockForegroundService — Duy trì tiến trình bảo vệ ngầm và cung cấp cơ chế
 * UsageStatsManager Fallback khi người dùng chưa bật AccessibilityService.
 */
@AndroidEntryPoint
class AppLockForegroundService : LifecycleService() {

    @Inject
    lateinit var sessionManager: UnlockSessionManager

    @Inject
    lateinit var overlayManager: LockOverlayManager

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                sessionManager.onScreenTurnedOff()
                overlayManager.dismissOverlay()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createProtectionNotification())
        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        startUsageStatsFallbackLoop()
    }

    private fun startUsageStatsFallbackLoop() {
        lifecycleScope.launch(Dispatchers.Default) {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            while (isActive) {
                // Nếu AccessibilityService đã bật thì nhường toàn quyền cho Accessibility để tiết kiệm pin
                if (!PermissionHelper.isAccessibilityServiceEnabled(this@AppLockForegroundService) &&
                    PermissionHelper.hasUsageStatsPermission(this@AppLockForegroundService)
                ) {
                    val topPkg = queryTopForegroundPackage(usageStatsManager)
                    if (!topPkg.isNullOrBlank() && sessionManager.shouldLockPackage(topPkg)) {
                        overlayManager.showLockOverlay(topPkg)
                    }
                    delay(350L)
                } else {
                    delay(2000L)
                }
            }
        }
    }

    private fun queryTopForegroundPackage(usageStatsManager: UsageStatsManager?): String? {
        usageStatsManager ?: return null
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 5_000L
        val events = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        var latestPackage: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                latestPackage = event.packageName
            }
        }
        return latestPackage
    }

    private fun createProtectionNotification(): Notification {
        val channelId = "serene_vault_protection_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Serene Vault Protection",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Duy trì trạng thái bảo vệ ứng dụng thời gian thực"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("LockLock đang bảo vệ")
            .setContentText("Serene Vault đang giám sát quyền riêng tư của bạn")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(screenOffReceiver) }
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 4041
    }
}
