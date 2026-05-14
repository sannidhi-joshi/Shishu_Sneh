package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shishu_sneh.data.local.entity.DailyLog
import com.example.shishu_sneh.ui.viewmodel.DailyLogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyLogScreen(navController: NavController, dailyLogViewModel: DailyLogViewModel) {
    val todayLog by dailyLogViewModel.todayLog.collectAsState()
    val allLogs by dailyLogViewModel.allLogs.collectAsState()
    val suggestion by dailyLogViewModel.suggestionText.collectAsState()

    var feeding by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf("") }
    var diaperCount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedLog by remember { mutableStateOf<DailyLog?>(null) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    androidx.compose.runtime.LaunchedEffect(todayLog?.id) {
        todayLog?.let { log ->
            feeding = log.feeding
            sleepHours = log.sleepHours.toString()
            diaperCount = log.diaperCount.toString()
            notes = log.notes
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE4E1), Color.White)
                )
            )
    ) {
        selectedLog?.let { log ->
            AlertDialog(
                onDismissRequest = { selectedLog = null },
                confirmButton = { TextButton(onClick = { selectedLog = null }) { Text("Close") } },
                title = { Text(dateFormatter.format(Date(log.date))) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Feeding: ${log.feeding}")
                        Text("Sleep: ${log.sleepHours} hours")
                        Text("Diapers: ${log.diaperCount}")
                        Text("Notes: ${log.notes.ifBlank { "-" }}")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📅 Daily Log", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Record feeding, sleep and daily baby care moments", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }

            item {
                suggestion?.let { text ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("💡 Suggestion", fontWeight = FontWeight.SemiBold)
                            Text(text, color = Color(0xFF3F5E7A))
                            TextButton(onClick = { navController.navigate("milestones") }) {
                                Text("Open Milestones")
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LogSection(
                            title = "🍼 Feeding",
                            value = feeding,
                            onValueChange = { feeding = it },
                            label = "Feeding"
                        )
                        LogSection(
                            title = "😴 Sleep",
                            value = sleepHours,
                            onValueChange = { sleepHours = it },
                            label = "Sleep Hours"
                        )
                        LogSection(
                            title = "🧷 Diaper",
                            value = diaperCount,
                            onValueChange = { diaperCount = it },
                            label = "Diaper Count"
                        )
                        LogSection(
                            title = "📝 Notes",
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notes",
                            singleLine = false,
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                dailyLogViewModel.saveLog(feeding, sleepHours, diaperCount, notes)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Log")
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("History", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        if (allLogs.isEmpty()) {
                            Text("No data yet. Add your first entry.", color = Color.Gray)
                        } else {
                            allLogs.sortedByDescending { it.date }.forEachIndexed { index, log ->
                                HistoryRow(
                                    log = log,
                                    dateLabel = dateFormatter.format(Date(log.date)),
                                    onClick = { selectedLog = log },
                                    highlight = index == 0
                                )
                                if (index != allLogs.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF0E4E7))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogSection(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Composable
private fun HistoryRow(
    log: DailyLog,
    dateLabel: String,
    onClick: () -> Unit,
    highlight: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (highlight) Color(0xFFFFF6F3) else Color(0xFFFDFDFD)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("📅")
            Column(modifier = Modifier.weight(1f)) {
                Text(dateLabel, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Sleep: ${log.sleepHours}h  •  Diapers: ${log.diaperCount}  •  Feeding: ${log.feeding.ifBlank { "-" }}",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
            if (highlight) {
                Text("New", color = Color(0xFFE07A9D), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
