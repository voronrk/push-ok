package com.example.microplanner.data

import com.example.microplanner.domain.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun initializeDatabase() {
        if (taskDao.getTaskCount() == 0) {
            taskDao.insertTasks(PredefinedTasks.getTasks())
        }
    }

    suspend fun getRandomAvailableTask(durationType: String, currentTime: Long): Task? {
        return taskDao.getRandomAvailableTask(durationType, currentTime)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deactivateTask(taskId: String) {
        taskDao.deactivateTask(taskId)
    }
    
    suspend fun restorePredefinedTasks() {
        taskDao.restorePredefinedTasks()
    }

    // Добавленные методы для корректной работы ViewModel
    suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }

    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }
}