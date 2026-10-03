package com.example.microplanner.data

import com.example.microplanner.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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

    suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }

    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun skipTaskUntilTomorrow(taskId: String) {
        taskDao.setSkippedDate(taskId, System.currentTimeMillis())
    }

    /**
     * Создаёт копию стандартного дела как пользовательское.
     * Оригинальное стандартное дело помечается как неактивное.
     * Копия наследует lastCompletedDate и lastSkippedDate от оригинала.
     * @return ID новой копии или null, если оригинал не найден или не является стандартным.
     */
    suspend fun createCopyFromPredefined(originalTaskId: String): String? {
        val original = taskDao.getTaskById(originalTaskId) ?: return null
        if (!original.isPredefined) return null

        val copyId = UUID.randomUUID().toString()
        val copy = original.copy(
            id = copyId,
            isPredefined = false,
            isActive = true
            // lastCompletedDate и lastSkippedDate наследуются автоматически через copy()
        )
        taskDao.insertTask(copy)
        taskDao.deactivateTask(originalTaskId)
        return copyId
    }
}