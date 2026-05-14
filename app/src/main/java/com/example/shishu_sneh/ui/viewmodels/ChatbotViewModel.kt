package com.example.shishu_sneh.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    fun sendMessage(query: String) {
        val currentList = _messages.value.toMutableList()
        currentList.add("User: $query")
        _messages.value = currentList

        viewModelScope.launch {
            // Rule-based fallback
            val reply = when {
                query.contains("fever", ignoreCase = true) -> "If your baby is under 3 months and has a fever, see a doctor immediately. Otherwise, ensure they are hydrated and rest."
                query.contains("vaccine", ignoreCase = true) -> "Check the vaccination schedule in the app. BCG, OPV, and Hepatitis B are given at birth."
                else -> "I'm a simple bot. Please consult a pediatrician for medical advice."
            }
            val updatedList = _messages.value.toMutableList()
            updatedList.add("Bot: $reply")
            _messages.value = updatedList
        }
    }
}
