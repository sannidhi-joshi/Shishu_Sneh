package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.shishu_sneh.ui.viewmodel.GrowthViewModel
import com.example.shishu_sneh.ui.viewmodel.VaccinationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.AxisBase
import android.graphics.Color as ChartColor
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun OnboardingScreen(navController: NavController, onGetStarted: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Shishu Sneh", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            onGetStarted()
            navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
        }) {
            Text("Get Started")
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    growthViewModel: GrowthViewModel,
    vaccinationViewModel: VaccinationViewModel
) {
    val growthRecords by growthViewModel.growthData.collectAsState()
    val vaccinations by vaccinationViewModel.vaccinations.collectAsState()
    val userProfile by growthViewModel.userProfile.collectAsState()
    val now = System.currentTimeMillis()
    val oneDayInMillis = 24L * 60 * 60 * 1000
    val upcomingVaccines = vaccinations
        .filter { !it.completed }
        .filter { it.dueDate - now <= oneDayInMillis }
        .sortedBy { it.dueDate }
        .take(3)
    val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    val babyName = userProfile?.babyName?.ifBlank { "Baby" } ?: "Baby"
    val ageMonths = userProfile?.dob?.takeIf { it > 0L }?.let {
        val diff = now - it
        (diff / (1000L * 60L * 60L * 24L * 30L)).toInt().coerceAtLeast(0)
    } ?: 0
    val totalVaccines = vaccinations.count()
    val upcomingCount = upcomingVaccines.size
    val recentGrowth = growthRecords.sortedByDescending { it.date }.take(2)
    val latestWeight = growthRecords.maxByOrNull { it.date }?.weight
    val latestHeight = growthRecords.maxByOrNull { it.date }?.height
    val growthStart = growthRecords.minByOrNull { it.date }
    val growthEnd = growthRecords.maxByOrNull { it.date }
    val milestoneProgressText = "${minOf(growthRecords.size, 5)}/5 achieved"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ComposeColor(0xFFFFE4E1), ComposeColor.White)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    onClick = { navController.navigate("feeding") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            ComposeColor(0xFFFFC1CC),
                                            ComposeColor(0xFFFFF3F0)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ComposeColor.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👶", style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(babyName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                            Text("$ageMonths months old", style = MaterialTheme.typography.bodyMedium, color = ComposeColor.Gray)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("growth_tracker") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("📈 Growth", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (latestWeight != null && latestHeight != null) "Weight ${latestWeight} kg  •  Height ${latestHeight} cm" else "Add entries to track growth trend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ComposeColor.Gray
                        )
                        GrowthPreviewChart(
                            growthRecords = growthRecords,
                            userDob = userProfile?.dob ?: 0L,
                            dateFormatter = dateFormatter
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("vaccination") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💉 Vaccination", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        if (upcomingVaccines.isEmpty()) {
                            Text("All vaccinations completed 🎉", style = MaterialTheme.typography.bodyMedium, color = ComposeColor.Gray)
                        } else {
                            upcomingVaccines.take(2).forEach { vaccine ->
                                Text(
                                    text = "• ${vaccine.name} - ${vaccinationViewModel.getDaysRemaining(vaccine.dueDate)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ComposeColor.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("milestones") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🧠 Milestones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(milestoneProgressText, style = MaterialTheme.typography.bodyMedium, color = ComposeColor.Gray)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("feeding") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🍼 Feeding", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text("Feeding guide, meal plan and reminders at a glance.", style = MaterialTheme.typography.bodyMedium, color = ComposeColor.Gray)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("insights") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📅 Daily Log", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text("Review daily routines, sleep and diaper trends.", style = MaterialTheme.typography.bodyMedium, color = ComposeColor.Gray)
                    }
                }
            }

            item {
                Text("Quick Access", style = MaterialTheme.typography.titleMedium, color = ComposeColor.Gray)
            }

            item {
                HomeActionCard(title = "Growth Tracker", emoji = "📈", onClick = { navController.navigate("growth_tracker") })
            }
            item {
                HomeActionCard(title = "Insights", emoji = "📊", onClick = { navController.navigate("insights") })
            }
            item {
                HomeActionCard(title = "AI Assistant", emoji = "🤖", onClick = { navController.navigate("chatbot") })
            }
        }
    }
}

@Composable
private fun GrowthPreviewChart(
    growthRecords: List<com.example.shishu_sneh.data.local.entity.GrowthRecord>,
    userDob: Long,
    dateFormatter: SimpleDateFormat
) {
    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    320
                )
                description.isEnabled = false
                setTouchEnabled(false)
                setScaleEnabled(false)
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val dayInMillis = 24L * 60L * 60L * 1000L
            fun toChartX(timestamp: Long): Float = (timestamp / dayInMillis).toFloat()

            val sortedRecords = growthRecords.sortedBy { it.date }
            Log.d(
                "GRAPH_DEBUG",
                sortedRecords.joinToString { record ->
                    "date=${dateFormatter.format(Date(record.date))}, weight=${record.weight}, height=${record.height}"
                }
            )

            val userEntries = sortedRecords.map { record ->
                Entry(toChartX(record.date), record.weight)
            }

            val userDataSet = LineDataSet(userEntries, "Your Baby").apply {
                color = ChartColor.rgb(255, 105, 135)
                setCircleColor(ChartColor.rgb(255, 105, 135))
                lineWidth = 2.2f
                circleRadius = 3.5f
                setDrawValues(false)
                setDrawCircles(true)
            }

            val whoEntries = mutableListOf<Entry>()
            if (userDob > 0L) {
                val whoData = com.example.shishu_sneh.data.local.WhoGrowthData.getWeightData("Female")
                whoData.forEach { pair ->
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = userDob
                        add(Calendar.MONTH, pair.first.toInt())
                    }
                    whoEntries.add(Entry(toChartX(calendar.timeInMillis), pair.second))
                }
            }

            val lineData = if (whoEntries.isNotEmpty()) {
                val whoDataSet = LineDataSet(whoEntries, "WHO").apply {
                    color = ChartColor.LTGRAY
                    setCircleColor(ChartColor.LTGRAY)
                    lineWidth = 1.6f
                    setDrawValues(false)
                    setDrawCircles(false)
                    enableDashedLine(10f, 5f, 0f)
                }
                LineData(userDataSet, whoDataSet)
            } else {
                LineData(userDataSet)
            }

            chart.data = lineData
            chart.xAxis.granularity = 1f
            chart.xAxis.isGranularityEnabled = true
            chart.xAxis.labelRotationAngle = -30f
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return dateFormatter.format(Date((value.toLong()) * dayInMillis))
                }
            }
            chart.notifyDataSetChanged()
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

@Composable
private fun HomeActionCard(title: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(ComposeColor(0xFFFFD7E5), ComposeColor(0xFFFFF2F7)))),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, style = MaterialTheme.typography.titleMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Tap to open", style = MaterialTheme.typography.bodySmall, color = ComposeColor.Gray)
            }
        }
    }
}
