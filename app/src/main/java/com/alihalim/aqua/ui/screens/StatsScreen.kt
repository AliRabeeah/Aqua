package com.alihalim.aqua.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alihalim.aqua.R
import com.alihalim.aqua.ui.AquaViewModel
import com.alihalim.aqua.ui.components.AquaCard
import com.alihalim.aqua.ui.components.StatsBarChart

@Composable
fun StatsScreen(viewModel: AquaViewModel) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val weekly by viewModel.weeklyStats.collectAsState()
    val monthly by viewModel.monthlyStats.collectAsState()
    val goal by viewModel.dailyGoalMl.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshStats() }

    val current = if (selectedTab == 0) weekly else monthly
    val nonEmpty = current.filter { it.totalMl > 0 }
    val average = if (current.isNotEmpty()) current.sumOf { it.totalMl } / current.size else 0
    val best = current.maxByOrNull { it.totalMl }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.stats_weekly)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.stats_monthly)) }
            )
        }

        Spacer(Modifier.height(24.dp))

        if (nonEmpty.isEmpty()) {
            AquaCard {
                Text(
                    text = stringResource(R.string.stats_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            AquaCard {
                StatsBarChart(
                    stats = current,
                    goalMl = goal,
                    barHeight = if (selectedTab == 0) 170 else 130,
                    showLabels = selectedTab == 0
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatTile(
                    label = stringResource(R.string.stats_average),
                    value = "$average ml",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = stringResource(R.string.stats_best_day),
                    value = "${best?.totalMl ?: 0} ml",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    AquaCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
