package com.alihalim.aqua.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alihalim.aqua.data.ReminderSettings
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    const val UNIQUE_WORK_NAME = "aqua_water_reminder"

    fun schedule(context: Context, settings: ReminderSettings) {
        val workManager = WorkManager.getInstance(context)
        if (!settings.enabled) {
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            return
        }
        val delayMinutes = computeInitialDelayMinutes(settings)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Computes the delay (in minutes) until the next reminder, honoring the
     * active-hours window. If a straightforward "interval from now" lands
     * outside the window, it jumps to the start of the next active window.
     */
    fun computeInitialDelayMinutes(settings: ReminderSettings): Long {
        val now = Calendar.getInstance()
        var candidate = now.clone() as Calendar
        candidate.add(Calendar.MINUTE, settings.intervalMinutes)

        val windowStart = now.clone() as Calendar
        windowStart.set(Calendar.HOUR_OF_DAY, settings.startHour)
        windowStart.set(Calendar.MINUTE, settings.startMinute)
        windowStart.set(Calendar.SECOND, 0)

        val windowEnd = now.clone() as Calendar
        windowEnd.set(Calendar.HOUR_OF_DAY, settings.endHour)
        windowEnd.set(Calendar.MINUTE, settings.endMinute)
        windowEnd.set(Calendar.SECOND, 0)

        val candidateMinutes = candidate.get(Calendar.HOUR_OF_DAY) * 60 + candidate.get(Calendar.MINUTE)
        val startMinutesOfDay = settings.startHour * 60 + settings.startMinute
        val endMinutesOfDay = settings.endHour * 60 + settings.endMinute

        val withinWindow = if (startMinutesOfDay <= endMinutesOfDay) {
            candidateMinutes in startMinutesOfDay..endMinutesOfDay
        } else {
            // window wraps past midnight
            candidateMinutes >= startMinutesOfDay || candidateMinutes <= endMinutesOfDay
        }

        if (!withinWindow) {
            // jump to next window start (today if still ahead, else tomorrow)
            candidate = windowStart.clone() as Calendar
            if (candidate.before(now)) {
                candidate.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val diffMillis = candidate.timeInMillis - now.timeInMillis
        return (diffMillis / (60 * 1000)).coerceAtLeast(1L)
    }
}
