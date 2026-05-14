package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shishu_sneh.ui.viewmodel.InsightsViewModel
import java.util.Locale

@Composable
fun InsightsScreen(navController: NavController, insightsViewModel: InsightsViewModel) {
    val logs by insightsViewModel.last7DaysLogs.collectAsState()
    val avgSleep by insightsViewModel.avgSleepHours.collectAsState()
    val avgDiaper by insightsViewModel.avgDiaperCount.collectAsState()
    val feedingConsistent by insightsViewModel.feedingConsistency.collectAsState()
    val sleepWarning by insightsViewModel.sleepWarning.collectAsState()
    val feedingWarning by insightsViewModel.feedingWarning.collectAsState()
    val sleepTrend by insightsViewModel.sleepTrend.collectAsState()
    val suggestion by insightsViewModel.activeDaySuggestion.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFE4E1), Color.White)))
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📊 Weekly Insights", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Last 7 days summary and trends", color = Color.Gray)
                    }
                }
            }

            if (logs.isEmpty()) {
                item {
                    EmptyStateCard("📝 Add daily logs to see insights")
                }
                item {
                    ActionCard(title = "📅 Start Daily Log", subtitle = "Record your baby's first entry", onClick = { navController.navigate("daily_log") })
                }
            } else {
                // Sleep Metrics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("😴 Sleep", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                                Text(sleepTrend, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            }
                            Text("${String.format(Locale.getDefault(), "%.1f", avgSleep)} hours average", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            LinearProgressIndicator(progress = (avgSleep.toFloat() / 14f).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                // Sleep Warning
                sleepWarning?.let {
                    item {
                        WarningCard(it)
                    }
                }

                // Diaper Metrics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🧷 Diaper Changes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("${String.format(Locale.getDefault(), "%.1f", avgDiaper)} per day", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }

                // Feeding Metrics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🍼 Feeding", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (feedingConsistent) "✓ Consistent feeding recorded" else "⚠ Missing some days",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (feedingConsistent) Color(0xFF4CAF50) else Color(0xFFFF6F00)
                            )
                        }
                    }
                }

                // Feeding Warning
                feedingWarning?.let {
                    item {
                        WarningCard(it)
                    }
                }

                // Smart Suggestion
                suggestion?.let {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAE6)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("✨ Suggestion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                                Text(it, color = Color(0xFF795548))
                            }
                        }
                    }
                }

                // Action to Daily Log
                item {
                    ActionCard(title = "📅 Daily Log", subtitle = "Add today's data or review history", onClick = { navController.navigate("daily_log") })
                }
            }
        }
    }
}

@Composable
private fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.Gray)
        }
    }
}

@Composable
private fun WarningCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(message, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFC62828), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text, color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
