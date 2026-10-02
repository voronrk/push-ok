package com.example.microplanner.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.microplanner.data.AppDatabase
import com.example.microplanner.data.TaskRepository
import com.example.microplanner.domain.model.DurationType
import com.example.microplanner.domain.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(database.taskDao())

    val allTasks = taskRepository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.taskDao().deleteTask(taskId)
        }
    }

    fun saveTask(
        taskId: String?,
        title: String,
        durationType: DurationType,
        periodicityDays: Int
    ) {
        viewModelScope.launch {
            val task = if (taskId != null) {
                // Редактирование существующего
                val existing = taskRepository.taskDao().getTaskById(taskId)
                existing?.copy(
                    title = title,
                    durationType = durationType,
                    periodicityDays = periodicityDays
                )
            } else {
                // Создание нового
                Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    durationType = durationType,
                    periodicityDays = periodicityDays,
                    isActive = true,
                    isPredefined = false,
                    lastCompletedDate = null
                )
            }
            if (task != null) {
                taskRepository.taskDao().insertTask(task)
            }
        }
    }
}