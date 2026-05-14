package com.example.shishu_sneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shishu_sneh.data.local.entity.UserProfile
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.entity.GrowthRecord
import com.example.shishu_sneh.data.repository.AppRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GrowthViewModel(private val repository: AppRepository) : ViewModel() {

    val growthData: StateFlow<List<GrowthRecord>> =
        repository.allGrowthRecordsAsc
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val userProfile: StateFlow<UserProfile?> =
        repository.anyUser
            .catch { emit(null) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun addGrowth(weight: Float, height: Float) {
        val record = GrowthRecord(weight = weight, height = height, date = System.currentTimeMillis())
        viewModelScope.launch {
            repository.insertGrowthRecord(record)
        }
    }

    fun addGrowth(weight: Float, height: Float, date: Long) {
        val record = GrowthRecord(weight = weight, height = height, date = date)
        viewModelScope.launch {
            repository.insertGrowthRecord(record)
        }
    }

    fun addGrowth(weight: Float, height: Float, date: Long, onComplete: (() -> Unit)? = null) {
        val record = GrowthRecord(weight = weight, height = height, date = date)
        viewModelScope.launch {
            repository.insertGrowthRecord(record)
            onComplete?.invoke()
        }
    }
}
