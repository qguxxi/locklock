package com.locklock.applock.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Phát hiện khi người dùng cài đặt một ứng dụng mới từ Google Play Store.
 */
class PackageInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
            if (!isReplacing) {
                val newPkg = intent.data?.schemeSpecificPart ?: return
                // Có thể mở rộng thông báo gợi ý khóa 1 chạm cho ứng dụng mới tại đây
            }
        }
    }
}
