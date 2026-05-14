package com.example.shishu_sneh.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.entity.UserProfile
import com.example.shishu_sneh.data.local.feeding.FeedingGuide
import com.example.shishu_sneh.data.local.feeding.FeedingGuideEngine
import com.example.shishu_sneh.data.local.feeding.FeedingReminderManager
import com.example.shishu_sneh.data.local.feeding.MealPlan
import com.example.shishu_sneh.data.local.feeding.NutritionInfo
import com.example.shishu_sneh.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow

class FeedingGuideViewModel(
    application: Application,
    repository: AppRepository
) : AndroidViewModel(application) {

    private val reminderManager = FeedingReminderManager(application.applicationContext)

    private val userProfile: StateFlow<UserProfile?> =
        repository.anyUser
            .catch { emit(null) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _reminderStatus = MutableStateFlow<String?>(null)
    val reminderStatus = _reminderStatus.asStateFlow()

    val ageInMonths: StateFlow<Int?> =
        userProfile
            .map { profile ->
                profile?.dob?.takeIf { it > 0L }?.let { FeedingGuideEngine.getAgeInMonths(it) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val guide: StateFlow<FeedingGuide?> =
        ageInMonths
            .map { age -> age?.let { FeedingGuideEngine.getFeedingGuide(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val mealPlan: StateFlow<MealPlan?> =
        ageInMonths
            .map { age -> age?.let { FeedingGuideEngine.getMealPlan(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val nutritionInfo: StateFlow<NutritionInfo?> =
        ageInMonths
            .map { age -> age?.let { FeedingGuideEngine.getNutritionInfo(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val smartSuggestions: StateFlow<List<String>> =
        ageInMonths
            .map { age ->
                age?.let { FeedingGuideEngine.getSmartSuggestions(it) } ?: emptyList()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setFeedingReminders() {
        val age = ageInMonths.value
        if (age == null) {
            _reminderStatus.value = "Baby age not available"
            return
        }

        reminderManager.scheduleDailyReminders(age)
        _reminderStatus.value = "Reminders enabled"
    }
}
