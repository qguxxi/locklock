package com.locklock.applock.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.locklock.applock.service.AppLockForegroundService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val serviceIntent = Intent(context, AppLockForegroundService::class.java)
            runCatching {
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
