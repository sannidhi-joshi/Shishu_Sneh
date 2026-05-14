package com.example.shishu_sneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.entity.DailyLog
import com.example.shishu_sneh.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyLogViewModel(private val repository: AppRepository) : ViewModel() {

    private val dayStartMillis: Long
        get() {
            val now = System.currentTimeMillis()
            val millisPerDay = 24L * 60L * 60L * 1000L
            return now - (now % millisPerDay)
        }

    private val _savingState = MutableStateFlow(false)
    val savingState: StateFlow<Boolean> = _savingState.asStateFlow()

    val allLogs: StateFlow<List<DailyLog>> =
        repository.allDailyLogs
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val todayLog: StateFlow<DailyLog?> =
        allLogs
            .map { logs -> logs.filter { it.date >= dayStartMillis }.maxByOrNull { it.date } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val suggestionText: StateFlow<String?> =
        todayLog
            .map { log ->
                when {
                    log == null -> null
                    log.sleepHours > 8f && log.feeding.isNotBlank() -> "Your baby had an active day. Consider adding a milestone."
                    log.notes.contains("walk", ignoreCase = true) ||
                        log.notes.contains("talk", ignoreCase = true) ||
                        log.notes.contains("first", ignoreCase = true) -> "Great moment detected. Consider adding a Custom Milestone."
                    else -> null
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun saveLog(feeding: String, sleepHours: String, diaperCount: String, notes: String) {
        val log = DailyLog(
            date = System.currentTimeMillis(),
            feeding = feeding.trim(),
            sleepHours = sleepHours.trim().toFloatOrNull() ?: 0f,
            diaperCount = diaperCount.trim().toIntOrNull() ?: 0,
            notes = notes.trim()
        )

        viewModelScope.launch {
            _savingState.value = true
            runCatching { repository.replaceTodayDailyLog(log, dayStartMillis) }
            _savingState.value = false
        }
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
