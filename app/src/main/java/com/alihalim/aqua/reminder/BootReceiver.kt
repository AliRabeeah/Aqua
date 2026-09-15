package com.alihalim.aqua.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alihalim.aqua.AquaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val app = context.applicationContext as AquaApplication
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val settings = app.settingsRepository.reminderSettings.first()
                ReminderScheduler.schedule(context.applicationContext, settings)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
