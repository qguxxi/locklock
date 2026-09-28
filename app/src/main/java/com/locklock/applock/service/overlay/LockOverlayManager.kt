package com.locklock.applock.service.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.locklock.applock.core.permission.PermissionHelper
import com.locklock.applock.designsystem.theme.SereneVaultTheme
import com.locklock.applock.service.UnlockSessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockOverlayManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: UnlockSessionManager
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var overlayView: ComposeView? = null
    private var overlayLifecycle: ComposeOverlayViewLifecycle? = null

    fun showLockOverlay(targetPackage: String) {
        mainScope.launch {
            if (!PermissionHelper.hasOverlayPermission(context)) return@launch
            if (overlayView != null) return@launch

            sessionManager.markVerifying(targetPackage)

            val lifecycleOwner = ComposeOverlayViewLifecycle().apply { onCreate() }
            val composeView = ComposeView(context).apply {
                lifecycleOwner.attachToView(this)
                setContent {
                    SereneVaultTheme {
                        // Màn hình khóa sẽ được thiết kế mới tại đây
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SereneVaultTheme.colors.slateBg)
                        )
                    }
                }
            }

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }

            runCatching {
                windowManager.addView(composeView, layoutParams)
                lifecycleOwner.onResume()
                overlayView = composeView
                overlayLifecycle = lifecycleOwner
            }.onFailure {
                sessionManager.markVerifying(null)
            }
        }
    }

    fun dismissOverlay() {
        mainScope.launch {
            overlayView?.let { view ->
                runCatching { windowManager.removeViewImmediate(view) }
            }
            overlayLifecycle?.onDestroy()
            overlayView = null
            overlayLifecycle = null
            sessionManager.markVerifying(null)
        }
    }
}
