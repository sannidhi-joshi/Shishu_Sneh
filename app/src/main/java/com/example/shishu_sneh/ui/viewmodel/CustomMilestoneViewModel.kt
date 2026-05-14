package com.example.shishu_sneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.dao.CustomMilestoneDao
import com.example.shishu_sneh.data.local.entity.CustomMilestone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomMilestoneViewModel(
    private val customMilestoneDao: CustomMilestoneDao
) : ViewModel() {

    private val _milestones = MutableStateFlow<List<CustomMilestone>>(emptyList())
    val milestones: StateFlow<List<CustomMilestone>> = _milestones.asStateFlow()

    init {
        loadMilestones()
    }

    fun loadMilestones() {
        viewModelScope.launch {
            _milestones.value = runCatching { customMilestoneDao.getAllMilestones() }
                .getOrDefault(emptyList())
        }
    }

    fun addMilestone(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            customMilestoneDao.insertMilestone(
                CustomMilestone(
                    title = title.trim(),
                    description = description.trim(),
                    date = System.currentTimeMillis()
                )
            )
            loadMilestones()
        }
    }
}
