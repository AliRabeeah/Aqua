package com.alihalim.aqua.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alihalim.aqua.ui.DayStat

@Composable
fun StatsBarChart(
    stats: List<DayStat>,
    goalMl: Int,
    modifier: Modifier = Modifier,
    barHeight: Int = 160,
    showLabels: Boolean = true
) {
    val maxValue = maxOf(stats.maxOfOrNull { it.totalMl } ?: 0, goalMl, 1)
    val accent = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        stats.forEach { stat ->
            val fraction by animateFloatAsState(
                targetValue = (stat.totalMl.toFloat() / maxValue).coerceIn(0f, 1f),
                animationSpec = tween(500),
                label = "bar"
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(track),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction.coerceAtLeast(0.01f))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (stat.isToday) accent else accent.copy(alpha = 0.55f)
                            )
                    )
                }

                if (showLabels) {
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (stat.isToday) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
