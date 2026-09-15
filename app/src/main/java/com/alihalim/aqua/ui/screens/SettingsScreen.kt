package com.alihalim.aqua.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alihalim.aqua.R
import com.alihalim.aqua.data.AppIcon
import com.alihalim.aqua.data.SettingsRepository
import com.alihalim.aqua.data.ThemeMode
import com.alihalim.aqua.ui.AquaViewModel
import com.alihalim.aqua.ui.components.AquaCard
import com.alihalim.aqua.ui.components.ColorSwatch
import com.alihalim.aqua.ui.components.SectionHeader
import com.alihalim.aqua.ui.components.SettingRow
import com.alihalim.aqua.ui.theme.parseHexColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: AquaViewModel,
    onOpenAbout: () -> Unit,
    onLanguageChanged: (String) -> Unit
) {
    val context = LocalContext.current

    val language by viewModel.languageCode.collectAsState()
    val theme by viewModel.themeMode.collectAsState()
    val accent by viewModel.accentHex.collectAsState()
    val goal by viewModel.dailyGoalMl.collectAsState()
    val icon by viewModel.appIcon.collectAsState()
    val reminders by viewModel.reminderSettings.collectAsState()

    var showGoalDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var intervalSlider by remember(reminders.intervalMinutes) {
        mutableFloatStateOf(reminders.intervalMinutes.toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        // ---------- GENERAL ----------
        SectionHeader(stringResource(R.string.settings_section_general))
        AquaCard {
            Column {
                Text(
                    text = stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoiceChip(
                        label = stringResource(R.string.settings_language_en),
                        selected = language == "en",
                        onClick = {
                            viewModel.setLanguage("en")
                            onLanguageChanged("en")
                        }
                    )
                    ChoiceChip(
                        label = stringResource(R.string.settings_language_ar),
                        selected = language == "ar",
                        onClick = {
                            viewModel.setLanguage("ar")
                            onLanguageChanged("ar")
                        }
                    )
                }

                Spacer(Modifier.height(18.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(6.dp))

                SettingRow(
                    title = stringResource(R.string.settings_daily_goal),
                    subtitle = "$goal ml",
                    onClick = { showGoalDialog = true },
                    trailing = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------- APPEARANCE ----------
        SectionHeader(stringResource(R.string.settings_section_appearance))
        AquaCard {
            Column {
                Text(
                    text = stringResource(R.string.settings_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoiceChip(
                        label = stringResource(R.string.settings_theme_system),
                        selected = theme == ThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                    )
                    ChoiceChip(
                        label = stringResource(R.string.settings_theme_light),
                        selected = theme == ThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                    )
                    ChoiceChip(
                        label = stringResource(R.string.settings_theme_dark),
                        selected = theme == ThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.settings_accent_color),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SettingsRepository.ACCENT_PRESETS.forEach { hex ->
                        ColorSwatch(
                            color = parseHexColor(hex),
                            selected = accent.equals(hex, ignoreCase = true),
                            onClick = { viewModel.setAccentColor(hex) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.settings_app_icon),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoiceChip(
                        label = stringResource(R.string.settings_icon_default),
                        selected = icon == AppIcon.DEFAULT,
                        onClick = { viewModel.setAppIcon(AppIcon.DEFAULT) }
                    )
                    ChoiceChip(
                        label = stringResource(R.string.settings_icon_wave),
                        selected = icon == AppIcon.WAVE,
                        onClick = { viewModel.setAppIcon(AppIcon.WAVE) }
                    )
                    ChoiceChip(
                        label = stringResource(R.string.settings_icon_ring),
                        selected = icon == AppIcon.RING,
                        onClick = { viewModel.setAppIcon(AppIcon.RING) }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------- REMINDERS ----------
        SectionHeader(stringResource(R.string.settings_section_reminders))
        AquaCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_reminders_enabled),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = reminders.enabled,
                        onCheckedChange = { viewModel.setRemindersEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (reminders.enabled) {
                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.settings_reminder_interval),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(
                            R.string.settings_reminder_interval_value,
                            intervalSlider.toInt()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Slider(
                        value = intervalSlider,
                        onValueChange = { intervalSlider = it },
                        onValueChangeFinished = {
                            viewModel.setReminderInterval(intervalSlider.toInt())
                        },
                        valueRange = 15f..240f,
                        steps = 14
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.settings_reminder_window),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimeChip(
                            label = stringResource(R.string.settings_reminder_from),
                            hour = reminders.startHour,
                            minute = reminders.startMinute,
                            modifier = Modifier.weight(1f)
                        ) { h, m ->
                            viewModel.setReminderWindow(h, m, reminders.endHour, reminders.endMinute)
                        }
                        TimeChip(
                            label = stringResource(R.string.settings_reminder_to),
                            hour = reminders.endHour,
                            minute = reminders.endMinute,
                            modifier = Modifier.weight(1f)
                        ) { h, m ->
                            viewModel.setReminderWindow(reminders.startHour, reminders.startMinute, h, m)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------- ABOUT ----------
        SectionHeader(stringResource(R.string.settings_section_about))
        AquaCard {
            SettingRow(
                title = stringResource(R.string.settings_about_link),
                onClick = onOpenAbout,
                trailing = {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    if (showGoalDialog) {
        AmountInputDialog(
            title = stringResource(R.string.goal_edit_title),
            hint = stringResource(R.string.goal_hint),
            confirmLabel = stringResource(R.string.action_save),
            initialValue = goal.toString(),
            onConfirm = {
                viewModel.setDailyGoal(it)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun TimeChip(
    label: String,
    hour: Int,
    minute: Int,
    modifier: Modifier = Modifier,
    onTimeSelected: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val formatted = String.format(java.util.Locale.US, "%02d:%02d", hour, minute)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        onTimeSelected(selectedHour, selectedMinute)
                    },
                    hour,
                    minute,
                    true
                ).show()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
