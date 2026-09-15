package com.alihalim.aqua

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.alihalim.aqua.data.SettingsRepository
import com.alihalim.aqua.data.WaterRepository
import com.alihalim.aqua.data.local.AppDatabase

class AquaApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val waterRepository: WaterRepository by lazy { WaterRepository(database.waterDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }

    companion object {
        const val REMINDER_CHANNEL_ID = "aqua_reminders"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.reminder_channel_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
