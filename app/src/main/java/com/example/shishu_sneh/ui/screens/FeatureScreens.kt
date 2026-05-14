package com.example.shishu_sneh.ui.screens

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.shishu_sneh.data.local.entity.Vaccination
import com.example.shishu_sneh.ui.viewmodel.VaccinationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



@Composable
fun VaccinationScreen(navController: NavController, vaccinationViewModel: VaccinationViewModel) {
    val vaccinations by vaccinationViewModel.vaccinationList.collectAsState()
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    var selectedVaccine by remember { mutableStateOf<Vaccination?>(null) }
    val calendar = Calendar.getInstance()
    val currentMonthIndex = calendar.get(Calendar.MONTH)
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    var selectedMonth by remember { mutableStateOf(months[currentMonthIndex]) }

    val upcomingVaccines = vaccinations
        .filter { !it.completed }
        .filter { getMonthFromDate(it.dueDate) == selectedMonth }
        .sortedBy { it.dueDate }

    val completedVaccines = vaccinations
        .filter { it.completed }
        .filter { getMonthFromDate(it.dueDate) == selectedMonth }
        .sortedByDescending { it.dueDate }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE4E1), Color.White)
                )
            )
    ) {
        if (selectedVaccine != null) {
            val vaccine = selectedVaccine!!
            AlertDialog(
                onDismissRequest = { selectedVaccine = null },
                confirmButton = {
                    TextButton(onClick = { selectedVaccine = null }) { Text("Close") }
                },
                title = { Text(vaccine.name) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Due: ${dateFormat.format(Date(vaccine.dueDate))}")
                        Text("Status: ${vaccinationStatusLabel(vaccine, currentTime)}")
                        Text(vaccinationViewModel.getDaysRemaining(vaccine.dueDate))
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
                        Text("💉 Vaccination", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Track your baby's immunization", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }

            item {
                MonthTimelineRow(
                    months = months,
                    selectedMonth = selectedMonth,
                    currentMonthIndex = currentMonthIndex,
                    onMonthSelected = { selectedMonth = it }
                )
            }

            item {
                if (vaccinations.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No vaccinations scheduled", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Upcoming Vaccinations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            upcomingVaccines.take(4).forEach { vaccine ->
                                VaccinationItemCard(
                                    vaccine = vaccine,
                                    currentTime = currentTime,
                                    daysRemainingText = vaccinationViewModel.getDaysRemaining(vaccine.dueDate),
                                    dateFormat = dateFormat,
                                    onCheckedChange = { isChecked ->
                                        vaccinationViewModel.updateVaccinationStatus(vaccine.id, isChecked)
                                    },
                                    onClick = { selectedVaccine = vaccine }
                                )
                            }
                        }
                    }
                }
            }

            if (completedVaccines.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Completed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            completedVaccines.take(4).forEach { vaccine ->
                                VaccinationItemCard(
                                    vaccine = vaccine,
                                    currentTime = currentTime,
                                    daysRemainingText = vaccinationViewModel.getDaysRemaining(vaccine.dueDate),
                                    dateFormat = dateFormat,
                                    onCheckedChange = { isChecked ->
                                        vaccinationViewModel.updateVaccinationStatus(vaccine.id, isChecked)
                                    },
                                    onClick = { selectedVaccine = vaccine }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthTimelineRow(
    months: List<String>,
    selectedMonth: String,
    currentMonthIndex: Int,
    onMonthSelected: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(months) { month ->
                val isSelected = month == selectedMonth
                val isCurrent = months.indexOf(month) == currentMonthIndex

                Text(
                    text = month,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onMonthSelected(month) }
                        .background(
                            if (isSelected) Color(0xFFFFC1CC)
                            else if (isCurrent) Color(0xFFFFE7EC)
                            else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontWeight = if (isSelected || isCurrent) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun VaccinationItemCard(
    vaccine: Vaccination,
    currentTime: Long,
    dateFormat: SimpleDateFormat,
    daysRemainingText: String,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val dueDate = dateFormat.format(Date(vaccine.dueDate))
    val statusColor = when {
        vaccine.completed -> Color(0xFF4CAF50)
        vaccine.dueDate < currentTime -> Color(0xFFFF9800)
        else -> Color(0xFF4A90E2)
    }
    val statusLabel = vaccinationStatusLabel(vaccine, currentTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Text("💉", style = MaterialTheme.typography.titleLarge)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vaccine.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (vaccine.completed) TextDecoration.LineThrough else TextDecoration.None,
                        color = Color(0xFF3D3D3D)
                    )
                    Text("Date: $dueDate", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(daysRemainingText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = statusLabel,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = statusColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Checkbox(
                checked = vaccine.completed,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

private fun vaccinationStatusLabel(vaccine: Vaccination, currentTime: Long): String {
    return when {
        vaccine.completed -> "Completed"
        vaccine.dueDate < currentTime -> "Due"
        else -> "Upcoming"
    }
}

private fun getMonthFromDate(timestamp: Long): String {
    return SimpleDateFormat("MMM", Locale.getDefault()).format(Date(timestamp))
}

@Composable
fun MilestoneScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Developmental Milestones", style = MaterialTheme.typography.headlineSmall)
    }
}
