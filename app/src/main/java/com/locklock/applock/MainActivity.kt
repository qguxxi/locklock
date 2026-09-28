package com.locklock.applock

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.locklock.applock.designsystem.theme.SereneVaultTheme
import com.locklock.applock.domain.model.LockMode
import com.locklock.applock.presentation.MainViewModel
import com.locklock.applock.presentation.onboarding.SereneOnboardingScreen
import com.locklock.applock.presentation.pin.PinEntryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableHighRefreshRate()

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashView = splashScreenViewProvider.view
            val iconView = splashScreenViewProvider.iconView

            val iconScaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 0.88f)
            val iconScaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 0.88f)
            val viewAlpha = ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f)

            AnimatorSet().apply {
                interpolator = DecelerateInterpolator()
                duration = 280L
                playTogether(iconScaleX, iconScaleY, viewAlpha)
                doOnEnd { splashScreenViewProvider.remove() }
                start()
            }
        }

        setContent {
            SereneVaultTheme {
                var onboardingCompleted by remember { mutableStateOf(false) }

                if (!onboardingCompleted) {
                    SereneOnboardingScreen(
                        onFinishOnboarding = {
                            onboardingCompleted = true
                        }
                    )
                } else {
                    PinEntryScreen(
                        onPinComplete = { newPin ->
                            viewModel.updatePasscode(newPin, LockMode.PIN)
                        }
                    )
                }
            }
        }
    }

    private fun enableHighRefreshRate() {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        } ?: return

        val highestMode = display.supportedModes.maxByOrNull { it.refreshRate } ?: return
        window.attributes = window.attributes.apply {
            preferredDisplayModeId = highestMode.modeId
            preferredRefreshRate = highestMode.refreshRate
        }
    }
}
