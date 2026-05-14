package com.example.shishu_sneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.entity.DailyLog
import com.example.shishu_sneh.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit

class InsightsViewModel(private val repository: AppRepository) : ViewModel() {

    private fun lastNDaysMillis(n: Int): Long {
        return System.currentTimeMillis() - TimeUnit.DAYS.toMillis(n.toLong())
    }

    val last7DaysLogs: StateFlow<List<DailyLog>> =
        repository.allDailyLogs
            .catch { emit(emptyList()) }
            .map { logs -> logs.filter { it.date >= lastNDaysMillis(7) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val avgSleepHours: StateFlow<Double> =
        last7DaysLogs
            .map { logs -> logs.map { it.sleepHours.toDouble() }.average().takeIf { !it.isNaN() } ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val totalDiapers: StateFlow<Int> =
        last7DaysLogs
            .map { logs -> logs.sumOf { it.diaperCount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val avgDiaperCount: StateFlow<Double> =
        last7DaysLogs
            .map { logs -> if (logs.isNotEmpty()) logs.map { it.diaperCount.toDouble() }.average().takeIf { !it.isNaN() } ?: 0.0 else 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val feedingConsistency: StateFlow<Boolean> =
        last7DaysLogs
            .map { logs -> logs.isNotEmpty() && logs.all { it.feeding.isNotBlank() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val sleepWarning: StateFlow<String?> =
        avgSleepHours
            .map { avg -> if (avg > 0 && avg < 6) "⚠ Low sleep detected (${String.format("%.1f", avg)} hours)" else null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val feedingWarning: StateFlow<String?> =
        feedingConsistency
            .map { consistent -> if (!consistent) "🍼 Feeding inconsistent - some days missing data" else null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val sleepTrend: StateFlow<String> =
        last7DaysLogs
            .map { logs ->
                if (logs.size < 2) return@map "—"
                val sorted = logs.sortedBy { it.date }
                val firstHalf = sorted.take(sorted.size / 2).map { it.sleepHours.toDouble() }.average()
                val secondHalf = sorted.drop(sorted.size / 2).map { it.sleepHours.toDouble() }.average()
                when {
                    secondHalf.isNaN() || firstHalf.isNaN() -> "—"
                    secondHalf > firstHalf + 0.5 -> "↗ Improving"
                    secondHalf < firstHalf - 0.5 -> "↘ Declining"
                    else -> "→ Stable"
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "—")

    val notesSummary: StateFlow<String> =
        last7DaysLogs
            .map { logs -> logs.mapNotNull { it.notes.takeIf { note -> note.isNotBlank() } }.joinToString("; ") }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val activeDaySuggestion: StateFlow<String?> =
        last7DaysLogs
            .map { logs ->
                val latest = logs.maxByOrNull { it.date }
                when {
                    latest == null -> null
                    latest.sleepHours > 8f && latest.feeding.isNotBlank() -> "Your baby had an active day. Consider adding a milestone."
                    latest.notes.contains("walk", ignoreCase = true) ||
                        latest.notes.contains("talk", ignoreCase = true) ||
                        latest.notes.contains("first", ignoreCase = true) -> "Great moment detected. Consider adding a Custom Milestone."
                    else -> null
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
