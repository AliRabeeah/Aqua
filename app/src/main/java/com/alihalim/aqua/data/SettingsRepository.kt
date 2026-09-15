package com.alihalim.aqua.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "aqua_settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class AppIcon(val aliasSuffix: String) {
    DEFAULT("IconDefault"),
    WAVE("IconWave"),
    RING("IconRing")
}

data class ReminderSettings(
    val enabled: Boolean,
    val intervalMinutes: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_code")
        val THEME = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color_hex")
        val DAILY_GOAL = intPreferencesKey("daily_goal_ml")
        val APP_ICON = stringPreferencesKey("app_icon")
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val REMINDER_INTERVAL = intPreferencesKey("reminder_interval_minutes")
        val REMINDER_START_HOUR = intPreferencesKey("reminder_start_hour")
        val REMINDER_START_MINUTE = intPreferencesKey("reminder_start_minute")
        val REMINDER_END_HOUR = intPreferencesKey("reminder_end_hour")
        val REMINDER_END_MINUTE = intPreferencesKey("reminder_end_minute")
    }

    companion object {
        const val DEFAULT_ACCENT = "#FF8A3D"
        const val DEFAULT_GOAL_ML = 2000
        val ACCENT_PRESETS = listOf(
            "#FF8A3D", // Aqua orange
            "#2FA4FF", // Blue
            "#2ED47A", // Green
            "#FF5C7A", // Coral
            "#B084F5", // Purple
            "#FFD34D", // Amber
            "#4DD9E8", // Teal
            "#FFFFFF"  // White
        )
    }

    val languageCode: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        runCatching { ThemeMode.valueOf(it[Keys.THEME] ?: ThemeMode.SYSTEM.name) }.getOrDefault(ThemeMode.SYSTEM)
    }

    val accentColorHex: Flow<String> = context.dataStore.data.map { it[Keys.ACCENT_COLOR] ?: DEFAULT_ACCENT }

    val dailyGoalMl: Flow<Int> = context.dataStore.data.map { it[Keys.DAILY_GOAL] ?: DEFAULT_GOAL_ML }

    val appIcon: Flow<AppIcon> = context.dataStore.data.map {
        runCatching { AppIcon.valueOf(it[Keys.APP_ICON] ?: AppIcon.DEFAULT.name) }.getOrDefault(AppIcon.DEFAULT)
    }

    val reminderSettings: Flow<ReminderSettings> = context.dataStore.data.map {
        ReminderSettings(
            enabled = it[Keys.REMINDERS_ENABLED] ?: true,
            intervalMinutes = it[Keys.REMINDER_INTERVAL] ?: 90,
            startHour = it[Keys.REMINDER_START_HOUR] ?: 8,
            startMinute = it[Keys.REMINDER_START_MINUTE] ?: 0,
            endHour = it[Keys.REMINDER_END_HOUR] ?: 23,
            endMinute = it[Keys.REMINDER_END_MINUTE] ?: 0
        )
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = code }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = mode.name }
    }

    suspend fun setAccentColor(hex: String) {
        context.dataStore.edit { it[Keys.ACCENT_COLOR] = hex }
    }

    suspend fun setDailyGoal(ml: Int) {
        context.dataStore.edit { it[Keys.DAILY_GOAL] = ml }
    }

    suspend fun setAppIcon(icon: AppIcon) {
        context.dataStore.edit { it[Keys.APP_ICON] = icon.name }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REMINDERS_ENABLED] = enabled }
    }

    suspend fun setReminderInterval(minutes: Int) {
        context.dataStore.edit { it[Keys.REMINDER_INTERVAL] = minutes }
    }

    suspend fun setReminderWindow(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        context.dataStore.edit {
            it[Keys.REMINDER_START_HOUR] = startHour
            it[Keys.REMINDER_START_MINUTE] = startMinute
            it[Keys.REMINDER_END_HOUR] = endHour
            it[Keys.REMINDER_END_MINUTE] = endMinute
        }
    }
}
