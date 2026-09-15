package com.alihalim.aqua.ui

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alihalim.aqua.AquaApplication
import com.alihalim.aqua.data.AppIcon
import com.alihalim.aqua.data.ReminderSettings
import com.alihalim.aqua.data.SettingsRepository
import com.alihalim.aqua.data.ThemeMode
import com.alihalim.aqua.data.local.WaterEntry
import com.alihalim.aqua.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayStat(val label: String, val totalMl: Int, val isToday: Boolean)

class AquaViewModel(app: Application) : AndroidViewModel(app) {

    private val aquaApp = app as AquaApplication
    private val water = aquaApp.waterRepository
    private val settings = aquaApp.settingsRepository

    // ---- Settings state ----
    val languageCode: StateFlow<String> =
        settings.languageCode.stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    val themeMode: StateFlow<ThemeMode> =
        settings.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val accentHex: StateFlow<String> =
        settings.accentColorHex.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsRepository.DEFAULT_ACCENT)

    val dailyGoalMl: StateFlow<Int> =
        settings.dailyGoalMl.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsRepository.DEFAULT_GOAL_ML)

    val appIcon: StateFlow<AppIcon> =
        settings.appIcon.stateIn(viewModelScope, SharingStarted.Eagerly, AppIcon.DEFAULT)

    val reminderSettings: StateFlow<ReminderSettings> =
        settings.reminderSettings.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ReminderSettings(true, 90, 8, 0, 23, 0)
        )

    // ---- Water state ----
    val todayTotalMl: StateFlow<Int> =
        water.todaysTotal().stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val todayEntries: StateFlow<List<WaterEntry>> =
        water.todaysEntries().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _weeklyStats = MutableStateFlow<List<DayStat>>(emptyList())
    val weeklyStats: StateFlow<List<DayStat>> = _weeklyStats.asStateFlow()

    private val _monthlyStats = MutableStateFlow<List<DayStat>>(emptyList())
    val monthlyStats: StateFlow<List<DayStat>> = _monthlyStats.asStateFlow()

    init {
        // Make sure reminders are scheduled according to saved settings on app start.
        viewModelScope.launch {
            settings.reminderSettings.collect { s ->
                ReminderScheduler.schedule(getApplication(), s)
            }
        }
        refreshStats()
    }

    // ---- Water actions ----
    fun addWater(amountMl: Int) {
        if (amountMl <= 0) return
        viewModelScope.launch {
            water.logWater(amountMl)
            refreshStats()
        }
    }

    fun deleteEntry(entry: WaterEntry) {
        viewModelScope.launch {
            water.deleteEntry(entry)
            refreshStats()
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val weekly = (6 downTo 0).map { offset ->
                DayStat(
                    label = dayLabel(offset),
                    totalMl = water.totalForDayOffset(offset),
                    isToday = offset == 0
                )
            }
            _weeklyStats.value = weekly

            val monthly = (29 downTo 0).map { offset ->
                DayStat(
                    label = dayOfMonthLabel(offset),
                    totalMl = water.totalForDayOffset(offset),
                    isToday = offset == 0
                )
            }
            _monthlyStats.value = monthly
        }
    }

    private fun dayLabel(daysAgo: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
        val fmt = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        return fmt.format(cal.time)
    }

    private fun dayOfMonthLabel(daysAgo: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
        return cal.get(java.util.Calendar.DAY_OF_MONTH).toString()
    }

    // ---- Settings actions ----
    fun setLanguage(code: String) = viewModelScope.launch { settings.setLanguage(code) }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { settings.setThemeMode(mode) }

    fun setAccentColor(hex: String) = viewModelScope.launch { settings.setAccentColor(hex) }

    fun setDailyGoal(ml: Int) = viewModelScope.launch {
        settings.setDailyGoal(ml.coerceIn(200, 10000))
    }

    fun setRemindersEnabled(enabled: Boolean) = viewModelScope.launch {
        settings.setRemindersEnabled(enabled)
    }

    fun setReminderInterval(minutes: Int) = viewModelScope.launch {
        settings.setReminderInterval(minutes.coerceIn(15, 480))
    }

    fun setReminderWindow(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) =
        viewModelScope.launch {
            settings.setReminderWindow(startHour, startMinute, endHour, endMinute)
        }

    fun setAppIcon(icon: AppIcon) = viewModelScope.launch {
        settings.setAppIcon(icon)
        applyLauncherIcon(icon)
    }

    /**
     * Enables the activity-alias for the chosen icon and disables the others.
     * The launcher may take a moment (or a relaunch) to refresh the icon.
     */
    private fun applyLauncherIcon(icon: AppIcon) {
        val context = getApplication<Application>()
        val pm = context.packageManager
        AppIcon.entries.forEach { candidate ->
            val component = ComponentName(context, "com.alihalim.aqua.${candidate.aliasSuffix}")
            val newState = if (candidate == icon) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            runCatching {
                pm.setComponentEnabledSetting(component, newState, PackageManager.DONT_KILL_APP)
            }
        }
    }
}
