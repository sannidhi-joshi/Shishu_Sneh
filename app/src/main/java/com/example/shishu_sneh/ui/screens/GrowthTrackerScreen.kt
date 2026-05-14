package com.example.shishu_sneh.ui.screens

import android.app.DatePickerDialog
import android.graphics.Color as ChartColor
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.shishu_sneh.data.local.WhoGrowthData
import com.example.shishu_sneh.data.local.entity.GrowthRecord
import com.example.shishu_sneh.ui.viewmodel.GrowthViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun GrowthTrackerScreen(navController: NavController, viewModel: GrowthViewModel, onSaved: (() -> Unit)? = null) {
    val growthRecords by viewModel.growthData.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val chartDateFormatter = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }

    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var dateText by remember { mutableStateOf(dateFormatter.format(Date(selectedDate))) }

    val latestRecord = growthRecords.maxByOrNull { it.date }
    val latestWeight = latestRecord?.weight
    val latestHeight = latestRecord?.height

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF1F5), Color(0xFFFDFDFD))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                SummaryMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Latest Weight",
                    value = latestWeight?.let { String.format(Locale.getDefault(), "%.1f kg", it) } ?: "--",
                    accent = Color(0xFFFF8CA8),
                    emoji = "⚖️"
                )
                SummaryMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Latest Height",
                    value = latestHeight?.let { String.format(Locale.getDefault(), "%.0f cm", it) } ?: "--",
                    accent = Color(0xFF7FB3FF),
                    emoji = "📏"
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Growth Progress", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Weight trend over time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A7A7A)
                    )

                    if (growthRecords.isEmpty()) {
                        EmptyGrowthState()
                    } else {
                        GrowthProgressChart(
                            growthRecords = growthRecords,
                            userDob = userProfile?.dob ?: 0L,
                            dateFormatter = chartDateFormatter
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Add New Growth Record", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = {},
                        label = { Text("Date") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val selectedCalendar = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            }
                                            selectedDate = selectedCalendar.timeInMillis
                                            dateText = dateFormatter.format(Date(selectedDate))
                                            errorMessage = null
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = {
                            weightText = it
                            errorMessage = null
                        },
                        label = { Text("Weight") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = { Text("kg", color = Color(0xFF7A7A7A), fontWeight = FontWeight.SemiBold) }
                    )

                    OutlinedTextField(
                        value = heightText,
                        onValueChange = {
                            heightText = it
                            errorMessage = null
                        },
                        label = { Text("Height") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = { Text("cm", color = Color(0xFF7A7A7A), fontWeight = FontWeight.SemiBold) }
                    )

                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            val weightValue = weightText.trim().replace(",", ".").toFloatOrNull()
                            val heightValue = heightText.trim().replace(",", ".").toFloatOrNull()

                            if (weightValue == null || heightValue == null) {
                                errorMessage = "Please enter valid numeric values"
                                return@Button
                            }

                            val dob = userProfile?.dob ?: 0L
                            if (dob > 0L && selectedDate < dob) {
                                errorMessage = "Selected date must be on or after baby's DOB"
                                return@Button
                            }

                            viewModel.addGrowth(weightValue, heightValue, selectedDate) {
                                Toast.makeText(context, "Growth record saved", Toast.LENGTH_SHORT).show()
                                if (!navController.popBackStack("home", false)) {
                                    navController.navigate("home") {
                                        launchSingleTop = true
                                    }
                                }
                                onSaved?.invoke()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8CA8))
                    ) {
                        Text("Save Growth Record", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFE1EA), Color(0xFFFFF7FA))
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.75f))
                        .padding(14.dp)
                ) {
                    Text("🧸", style = MaterialTheme.typography.headlineSmall)
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Growth Tracker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Monitor your baby's healthy growth journey",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6E6E6E)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Color,
    emoji: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f))
                    .padding(10.dp)
            ) {
                Text(emoji, style = MaterialTheme.typography.titleMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7A7A7A))
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyGrowthState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFFE1EA))
                    .padding(18.dp)
            ) {
                Text("👶", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = "No growth records yet",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Add your baby's first measurement to see the growth trend here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A7A7A),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GrowthProgressChart(
    growthRecords: List<GrowthRecord>,
    userDob: Long,
    dateFormatter: SimpleDateFormat
) {
    AndroidView(
        factory = {
            LineChart(it).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                description = Description().apply { isEnabled = false }
                setTouchEnabled(true)
                setPinchZoom(false)
                setScaleEnabled(false)
                isDoubleTapToZoomEnabled = false
                setDrawGridBackground(false)
                setBackgroundColor(ChartColor.TRANSPARENT)
                axisRight.isEnabled = false
                axisLeft.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                xAxis.isGranularityEnabled = true
                xAxis.textColor = ChartColor.DKGRAY
                axisLeft.textColor = ChartColor.DKGRAY
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.textSize = 10f
                animateX(500)
            }
        },
        update = { chart ->
            val dayInMillis = 24L * 60L * 60L * 1000L

            fun toChartX(timestamp: Long): Float = (timestamp / dayInMillis).toFloat()

            val sortedRecords = growthRecords.sortedBy { it.date }
            val userEntries = sortedRecords.map { record ->
                Entry(toChartX(record.date), record.weight)
            }

            val userDataSet = LineDataSet(userEntries, "Your baby").apply {
                color = ChartColor.rgb(255, 126, 155)
                setCircleColor(ChartColor.rgb(255, 126, 155))
                setDrawCircleHole(true)
                circleHoleColor = ChartColor.WHITE
                lineWidth = 3f
                circleRadius = 4.5f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(false)
                setDrawFilled(true)
                fillColor = ChartColor.rgb(255, 126, 155)
                fillAlpha = 45
                highLightColor = ChartColor.rgb(255, 126, 155)
            }

            val whoEntries = mutableListOf<Entry>()
            if (userDob > 0L) {
                WhoGrowthData.getWeightData("Female").forEach { pair ->
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
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                    mode = LineDataSet.Mode.LINEAR
                    enableDashedLine(12f, 8f, 0f)
                }
                LineData(userDataSet, whoDataSet)
            } else {
                LineData(userDataSet)
            }

            chart.data = lineData
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
            .height(260.dp)
    )
}
