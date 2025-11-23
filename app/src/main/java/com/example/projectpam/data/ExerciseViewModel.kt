package com.example.projectpam.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class ExerciseUiState(
    val isLoading: Boolean = false,
    val exercises: List<ExerciseUi> = emptyList(),
    val error: String? = null
)

class ExerciseViewModel : ViewModel() {

    var uiState = mutableStateOf(ExerciseUiState())
        private set

    private val repo = ExerciseRepository

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)
            try {
                val items = repo.loadExercises()
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    exercises = items
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun addExercise(name: String, durationMin: Int, timeDisplay: String) {
        viewModelScope.launch {
            try {
                repo.addExercise(name, durationMin, timeDisplay)
                refresh()
            } catch (_: Exception) {
                // boleh tulis log / update error state
            }
        }
    }

    fun updateExercise(exercise: ExerciseUi) {
        viewModelScope.launch {
            try {
                repo.updateExercise(exercise)
                refresh()
            } catch (_: Exception) {
            }
        }
    }

    fun deleteExercise(id: Int) {
        viewModelScope.launch {
            try {
                repo.deleteExercise(id)
                refresh()
            } catch (_: Exception) {
            }
        }
    }
}
