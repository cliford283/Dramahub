package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DramaCard
import com.example.ui.theme.DramaCardElevated
import com.example.ui.theme.DramaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class GrowthPoint(
    val dateLabel: String,
    val userCount: Float,
    val activeViewers: Float,
    val vipConversions: Float
)

data class SeriesMetric(
    val title: String,
    val views: Long,
    val completionRate: Float, // 0..100
    val streamHours: Int,
    val color: Color
)

/**
 * Enterprise Recharts-style Data Visualization Component for the Admin Dashboard.
 * Displays interactive User Growth Area Chart, Series Performance Bar Chart,
 * and Subscription Conversion Donut Breakdown.
 */
@Composable
fun AdminMetricsChart(
    modifier: Modifier = Modifier,
    totalUsersCount: Int = 124,
    vipCount: Int = 42
) {
    var selectedChartType by remember { mutableIntStateOf(0) } // 0: Growth Trend, 1: Series Performance, 2: VIP Breakdown
    var timeframe by remember { mutableStateOf("30D") } // "7D", "30D", "90D"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DramaCard),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_metrics_chart_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chart Header & Tab Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DramaRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (selectedChartType) {
                                0 -> Icons.Default.AutoGraph
                                1 -> Icons.Default.BarChart
                                else -> Icons.Default.DonutLarge
                            },
                            contentDescription = null,
                            tint = DramaRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (selectedChartType) {
                                0 -> "User Growth & Engagement"
                                1 -> "Series Performance Index"
                                else -> "Subscriber Tier Distribution"
                            },
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Live Analytics • Recharts Engine",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Timeframe Selector
                if (selectedChartType == 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DramaCardElevated)
                            .padding(2.dp)
                    ) {
                        listOf("7D", "30D", "90D").forEach { tf ->
                            val isSelected = timeframe == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) DramaRed else Color.Transparent)
                                    .clickable { timeframe = tf }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tf,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Tabs (Growth, Performance, Tiers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1B1724))
                    .padding(3.dp)
            ) {
                ChartTabButton(
                    title = "User Growth",
                    isSelected = selectedChartType == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedChartType = 0 }

                ChartTabButton(
                    title = "Series Ranked",
                    isSelected = selectedChartType == 1,
                    modifier = Modifier.weight(1f)
                ) { selectedChartType = 1 }

                ChartTabButton(
                    title = "VIP Ratio",
                    isSelected = selectedChartType == 2,
                    modifier = Modifier.weight(1f)
                ) { selectedChartType = 2 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Chart Display
            when (selectedChartType) {
                0 -> UserGrowthAreaChart(timeframe = timeframe, totalUsers = totalUsersCount)
                1 -> SeriesPerformanceBarChart()
                2 -> SubscriptionDonutChart(totalUsers = totalUsersCount, vipCount = vipCount)
            }
        }
    }
}

@Composable
private fun ChartTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) DramaCardElevated else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) TextPrimary else TextMuted,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Interactive Recharts-style Area Chart for User Growth with gradient fill and scrubber tooltip.
 */
@Composable
fun UserGrowthAreaChart(timeframe: String, totalUsers: Int) {
    val dataPoints = remember(timeframe, totalUsers) {
        generateGrowthData(timeframe, totalUsers)
    }

    var activeIndex by remember { mutableIntStateOf(dataPoints.size - 1) }
    var touchX by remember { mutableFloatStateOf(-1f) }

    val activePoint = dataPoints.getOrNull(activeIndex) ?: dataPoints.last()

    Column {
        // Metric summary readout (Recharts tooltip header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${activePoint.userCount.roundToInt()} Total Users",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+24.8% vs previous month • ${activePoint.dateLabel}",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Legend indicators
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendDot(color = DramaRed, label = "Users")
                Spacer(modifier = Modifier.width(10.dp))
                LegendDot(color = Color(0xFF00E5FF), label = "Active Viewers")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Canvas Area Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        touchX = offset.x
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        activeIndex = (fraction * (dataPoints.size - 1)).roundToInt()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        touchX = change.position.x
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        activeIndex = (fraction * (dataPoints.size - 1)).roundToInt()
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val maxVal = (dataPoints.maxOfOrNull { it.userCount } ?: 100f) * 1.15f
                val minVal = 0f

                // Draw background horizontal gridlines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color(0xFF2B2538),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // Compute points
                val userPoints = dataPoints.mapIndexed { index, p ->
                    val x = (index.toFloat() / (dataPoints.size - 1)) * width
                    val y = height - ((p.userCount - minVal) / (maxVal - minVal)) * height
                    Offset(x, y)
                }

                val activePoints = dataPoints.mapIndexed { index, p ->
                    val x = (index.toFloat() / (dataPoints.size - 1)) * width
                    val y = height - ((p.activeViewers - minVal) / (maxVal - minVal)) * height
                    Offset(x, y)
                }

                // 1. Draw Active Viewers Area (Cyan gradient)
                val activeAreaPath = Path().apply {
                    moveTo(activePoints.first().x, height)
                    activePoints.forEach { lineTo(it.x, it.y) }
                    lineTo(activePoints.last().x, height)
                    close()
                }
                drawPath(
                    path = activeAreaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.25f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Active line
                val activeLinePath = Path().apply {
                    moveTo(activePoints.first().x, activePoints.first().y)
                    for (i in 1 until activePoints.size) {
                        val prev = activePoints[i - 1]
                        val curr = activePoints[i]
                        val cx = (prev.x + curr.x) / 2
                        cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                    }
                }
                drawPath(
                    path = activeLinePath,
                    color = Color(0xFF00E5FF),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )

                // 2. Draw Total Users Area (Crimson Red gradient)
                val userAreaPath = Path().apply {
                    moveTo(userPoints.first().x, height)
                    userPoints.forEach { lineTo(it.x, it.y) }
                    lineTo(userPoints.last().x, height)
                    close()
                }
                drawPath(
                    path = userAreaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(DramaRed.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // User Spline Line
                val userLinePath = Path().apply {
                    moveTo(userPoints.first().x, userPoints.first().y)
                    for (i in 1 until userPoints.size) {
                        val prev = userPoints[i - 1]
                        val curr = userPoints[i]
                        val cx = (prev.x + curr.x) / 2
                        cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                    }
                }
                drawPath(
                    path = userLinePath,
                    color = DramaRed,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // 3. Draw active cursor scrubber line
                val activeX = userPoints[activeIndex].x
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(activeX, 0f),
                    end = Offset(activeX, height),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Scrubber Dots
                drawCircle(
                    color = DramaRed,
                    radius = 5.dp.toPx(),
                    center = userPoints[activeIndex]
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = userPoints[activeIndex]
                )

                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = 4.dp.toPx(),
                    center = activePoints[activeIndex]
                )
            }
        }

        // X-Axis Date Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val step = (dataPoints.size / 5).coerceAtLeast(1)
            for (i in dataPoints.indices step step) {
                Text(
                    text = dataPoints[i].dateLabel,
                    color = if (i == activeIndex) DramaRed else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = if (i == activeIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Recharts-style Series Performance Bar Chart.
 */
@Composable
fun SeriesPerformanceBarChart() {
    val series = remember {
        listOf(
            SeriesMetric("Mafia Don Hired Me As Wife", 3820000L, 88.4f, 1240, DramaRed),
            SeriesMetric("She Quit and Told 140M Deal", 2100000L, 82.1f, 890, Color(0xFFFF9F1C)),
            SeriesMetric("Kneel Before Dragon Queen", 4500000L, 94.2f, 1560, Color(0xFF00E5FF)),
            SeriesMetric("Weakest Bastard Shakes World", 3100000L, 85.7f, 1020, Color(0xFF7C4DFF)),
            SeriesMetric("The Outcast Queen", 2800000L, 79.5f, 740, Color(0xFF00E676))
        )
    }

    var selectedMetric by remember { mutableStateOf("Views") } // "Views", "Completion", "Hours"
    val numFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Ranking Drama Series",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Row {
                listOf("Views", "Completion", "Hours").forEach { metric ->
                    val isSel = selectedMetric == metric
                    Text(
                        text = metric,
                        color = if (isSel) DramaRed else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable { selectedMetric = metric }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val maxVal = when (selectedMetric) {
            "Views" -> series.maxOf { it.views }.toFloat()
            "Completion" -> 100f
            else -> series.maxOf { it.streamHours }.toFloat()
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            series.forEachIndexed { index, item ->
                val ratio = when (selectedMetric) {
                    "Views" -> item.views.toFloat() / maxVal
                    "Completion" -> item.completionRate / maxVal
                    else -> item.streamHours.toFloat() / maxVal
                }

                val animatedRatio by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                    label = "bar_ratio"
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${index + 1} ${item.title}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Text(
                            text = when (selectedMetric) {
                                "Views" -> "${(item.views / 1000000f).let { "%.1fM".format(it) }} views"
                                "Completion" -> "${item.completionRate}%"
                                else -> "${numFormat.format(item.streamHours)} hrs"
                            },
                            color = item.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Horizontal bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF262032))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedRatio)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(item.color.copy(alpha = 0.7f), item.color)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Recharts-style Donut / Radial Chart for Subscription Tiers.
 */
@Composable
fun SubscriptionDonutChart(totalUsers: Int, vipCount: Int) {
    val freeUsers = (totalUsers - vipCount).coerceAtLeast(0)
    val vipAnnual = (vipCount * 0.35f).roundToInt().coerceAtLeast(1)
    val vipMonthly = (vipCount - vipAnnual).coerceAtLeast(1)

    val segments = listOf(
        Triple("Free Tier", freeUsers, Color(0xFF6B7280)),
        Triple("VIP Monthly", vipMonthly, Color(0xFFFF9F1C)),
        Triple("VIP Annual", vipAnnual, DramaRed)
    )

    val sum = segments.sumOf { it.second }.toFloat().coerceAtLeast(1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Canvas Donut
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 22.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                var startAngle = -90f
                for (seg in segments) {
                    val sweep = (seg.second / sum) * 360f
                    drawArc(
                        color = seg.third,
                        startAngle = startAngle,
                        sweepAngle = sweep - 2f, // slight separator gap
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweep
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val vipPct = ((vipCount.toFloat() / totalUsers.coerceAtLeast(1)) * 100).roundToInt()
                Text(
                    text = "$vipPct%",
                    color = DramaRed,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "VIP Rate",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Breakdown Legend
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            segments.forEach { (name, count, color) ->
                val pct = ((count / sum) * 100).roundToInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "$count ($pct%)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

private fun generateGrowthData(timeframe: String, baseUsers: Int): List<GrowthPoint> {
    val count = when (timeframe) {
        "7D" -> 7
        "30D" -> 14
        else -> 20
    }

    val list = mutableListOf<GrowthPoint>()
    val step = (baseUsers * 0.7f) / count
    var currentUsers = (baseUsers * 0.3f)

    for (i in 0 until count) {
        val jitter = (i % 3 - 1) * 2f
        currentUsers += step + jitter
        val active = currentUsers * (0.68f + (i % 4) * 0.03f)
        val vip = currentUsers * (0.32f + (i % 3) * 0.02f)
        val dayLabel = when (timeframe) {
            "7D" -> "Day ${i + 1}"
            "30D" -> "Wk ${(i / 3) + 1} • D${(i % 3) + 1}"
            else -> "M${(i / 7) + 1} D${(i % 7) * 4 + 1}"
        }
        list.add(GrowthPoint(dayLabel, currentUsers.coerceAtLeast(10f), active, vip))
    }
    return list
}
