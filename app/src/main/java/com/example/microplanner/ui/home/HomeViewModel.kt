package com.example.microplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microplanner.data.AppStats
import com.example.microplanner.data.StatsRepository
import com.example.microplanner.data.TaskRepository
import com.example.microplanner.domain.model.DurationType
import com.example.microplanner.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class TaskDisplayState {
    object Idle : TaskDisplayState()
    data class ReadyToStart(val task: Task) : TaskDisplayState()
    data class InProgress(val task: Task, val startTime: Long) : TaskDisplayState()
    data class Completed(val minutesSpent: Int) : TaskDisplayState()
}

data class HomeUiState(
    val stats: AppStats = AppStats(),
    val taskDisplayState: TaskDisplayState = TaskDisplayState.Idle,
    val abGroup: String = "A"
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.initializeDatabase()
            val group = statsRepository.getOrAssignABGroup()
            _uiState.update { it.copy(abGroup = group) }
            
            viewModelScope.launch {
                statsRepository.statsFlow.collect { stats ->
                    _uiState.update { it.copy(stats = stats) }
                }
            }
        }
    }

    fun getRandomTask(durationType: DurationType) {
        viewModelScope.launch {
            val task = taskRepository.getRandomAvailableTask(durationType.name, System.currentTimeMillis())
            _uiState.update { 
                it.copy(taskDisplayState = if (task != null) TaskDisplayState.ReadyToStart(task) else TaskDisplayState.Idle) 
            }
        }
    }

    fun startTask() {
        val currentState = _uiState.value.taskDisplayState
        if (currentState is TaskDisplayState.ReadyToStart) {
            _uiState.update { 
                it.copy(taskDisplayState = TaskDisplayState.InProgress(currentState.task, System.currentTimeMillis())) 
            }
        }
    }

    fun completeTask() {
        viewModelScope.launch {
            val currentState = _uiState.value.taskDisplayState
            if (currentState is TaskDisplayState.InProgress) {
                val endTime = System.currentTimeMillis()
                val minutesSpent = ((endTime - currentState.startTime) / 60000).toInt().coerceAtLeast(1)
                
                val updatedTask = currentState.task.copy(lastCompletedDate = endTime)
                taskRepository.updateTask(updatedTask)
                statsRepository.incrementCompleted(minutesSpent)
                
                _uiState.update { it.copy(taskDisplayState = TaskDisplayState.Completed(minutesSpent)) }
            }
        }
    }

    fun skipTask() {
        viewModelScope.launch {
            statsRepository.incrementSkipped()
            _uiState.update { it.copy(taskDisplayState = TaskDisplayState.Idle) }
        }
    }

    fun notDoneTask() {
        viewModelScope.launch {
            statsRepository.resetStreak()
            _uiState.update { it.copy(taskDisplayState = TaskDisplayState.Idle) }
        }
    }

    fun markAsIrrelevant() {
        viewModelScope.launch {
            val currentState = _uiState.value.taskDisplayState
            
            // Безопасное извлечение задачи через when
            val taskToDeactivate = when (currentState) {
                is TaskDisplayState.ReadyToStart -> currentState.task
                is TaskDisplayState.InProgress -> currentState.task
                else -> null
            }

            if (taskToDeactivate != null) {
                taskRepository.deactivateTask(taskToDeactivate.id)
                _uiState.update { it.copy(taskDisplayState = TaskDisplayState.Idle) }
            }
        }
    }

    fun closeTaskCard() {
        _uiState.update { it.copy(taskDisplayState = TaskDisplayState.Idle) }
    }
}