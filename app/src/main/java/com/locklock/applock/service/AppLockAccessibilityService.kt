package com.locklock.applock.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import com.locklock.applock.service.overlay.LockOverlayManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AppLockAccessibilityService — Nhận diện ứng dụng mở theo thời gian thực với độ trễ 0ms.
 * Kết hợp với [UnlockSessionManager] để loại bỏ hoàn toàn hiện tượng lặp màn hình khóa.
 */
@AndroidEntryPoint
class AppLockAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var sessionManager: UnlockSessionManager

    @Inject
    lateinit var overlayManager: LockOverlayManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                sessionManager.onScreenTurnedOff()
                overlayManager.dismissOverlay()
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkgName = event.packageName?.toString() ?: return

        serviceScope.launch {
            if (sessionManager.shouldLockPackage(pkgName)) {
                overlayManager.showLockOverlay(pkgName)
            }
        }
    }

    override fun onInterrupt() {
        overlayManager.dismissOverlay()
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(screenOffReceiver) }
        serviceScope.cancel()
        super.onDestroy()
    }
}
