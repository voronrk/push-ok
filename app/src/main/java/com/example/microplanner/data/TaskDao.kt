package com.example.microplanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.microplanner.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Получить все дела (для экрана "Мои дела")
    @Query("SELECT * FROM tasks ORDER BY isPredefined DESC, title ASC")
    fun getAllTasks(): Flow<List<Task>>

    // Получить дело по ID (для редактирования)
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    // Вставить или обновить одно дело
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    // Вставить список дел (для инициализации стандартной базы)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTasks(tasks: List<Task>)

    // Обновить дело (например, после выполнения)
    @Update
    suspend fun updateTask(task: Task)

    // Удалить дело (только пользовательские)
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    // Пометить дело как неактивное
    @Query("UPDATE tasks SET isActive = 0 WHERE id = :taskId")
    suspend fun deactivateTask(taskId: String)

    // Восстановить все стандартные дела
    @Query("UPDATE tasks SET isActive = 1 WHERE isPredefined = 1")
    suspend fun restorePredefinedTasks()

    // Получить случайное доступное дело нужной длительности
    // Проверка: дело активно И (никогда не выполнялось ИЛИ прошло >= periodicityDays дней)
    // 86400000 мс = 1 день
    @Query("""
        SELECT * FROM tasks 
        WHERE isActive = 1 
          AND durationType = :durationType 
          AND (lastCompletedDate IS NULL OR (( :currentTime - lastCompletedDate ) / 86400000 >= periodicityDays))
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomAvailableTask(durationType: String, currentTime: Long): Task?
}