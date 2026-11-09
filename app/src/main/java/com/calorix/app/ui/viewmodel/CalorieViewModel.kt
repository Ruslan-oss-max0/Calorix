package com.calorix.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calorix.app.data.FoodEntry
import com.calorix.app.data.FoodRepository
import com.calorix.app.data.MealType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalorieViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalorieUiState(isLoading = true))
    val uiState: StateFlow<CalorieUiState> = _uiState.asStateFlow()

    private val todayDateString = FoodEntry.getFormattedDate(System.currentTimeMillis())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.dailyGoalFlow,
                repository.getEntriesForDate(todayDateString),
                repository.getAllEntries()
            ) { goal, todayList, allList ->
                val todayTotal = todayList.sumOf { it.calories }
                val breakdown = calculateBreakdown(todayList)

                CalorieUiState(
                    dailyGoal = goal,
                    todayEntries = todayList,
                    allEntries = allList,
                    todayTotalCalories = todayTotal,
                    mealBreakdown = breakdown,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { current ->
                    newState.copy(userFeedbackMessage = current.userFeedbackMessage)
                }
            }
        }
    }

    private fun calculateBreakdown(entries: List<FoodEntry>): MealBreakdown {
        var b = 0
        var l = 0
        var d = 0
        var s = 0

        entries.forEach { entry ->
            when (entry.mealType) {
                MealType.BREAKFAST -> b += entry.calories
                MealType.LUNCH -> l += entry.calories
                MealType.DINNER -> d += entry.calories
                MealType.SNACK -> s += entry.calories
            }
        }
        return MealBreakdown(breakfast = b, lunch = l, dinner = d, snack = s)
    }

    fun addFood(name: String, calories: Int, mealType: MealType): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || calories <= 0) {
            _uiState.update { it.copy(userFeedbackMessage = "Please enter a valid food name and calories > 0") }
            return false
        }

        viewModelScope.launch {
            repository.insertEntry(trimmed, calories, mealType)
            _uiState.update { it.copy(userFeedbackMessage = "Added $trimmed ($calories kcal)") }
        }
        return true
    }

    fun deleteFood(entry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
            _uiState.update { it.copy(userFeedbackMessage = "Removed ${entry.name}") }
        }
    }

    fun setDailyGoal(goal: Int): Boolean {
        if (goal < 500 || goal > 10000) {
            _uiState.update { it.copy(userFeedbackMessage = "Goal should be between 500 and 10,000 kcal") }
            return false
        }
        viewModelScope.launch {
            repository.setDailyCalorieGoal(goal)
            _uiState.update { it.copy(userFeedbackMessage = "Daily goal updated to $goal kcal") }
        }
        return true
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
            _uiState.update { it.copy(userFeedbackMessage = "All food records cleared") }
        }
    }

    class Factory(private val repository: FoodRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalorieViewModel::class.java)) {
                return CalorieViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
