package com.locklock.applock.core.disguise

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.locklock.applock.domain.model.DisguiseIconMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppIconDisguiseManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun applyDisguise(targetMode: DisguiseIconMode) {
        val pm = context.packageManager
        DisguiseIconMode.entries.forEach { mode ->
            val component = ComponentName(context.packageName, mode.aliasClassName)
            val newState = if (mode == targetMode) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            runCatching {
                pm.setComponentEnabledSetting(
                    component,
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}
