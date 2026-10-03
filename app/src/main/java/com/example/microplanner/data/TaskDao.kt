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

    @Query("SELECT * FROM tasks ORDER BY isPredefined DESC, title ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("UPDATE tasks SET isActive = 0 WHERE id = :taskId")
    suspend fun deactivateTask(taskId: String)

    @Query("UPDATE tasks SET isActive = 1 WHERE isPredefined = 1")
    suspend fun restorePredefinedTasks()

    @Query("""
        SELECT * FROM tasks 
        WHERE isActive = 1 
          AND durationType = :durationType 
          AND (lastCompletedDate IS NULL OR ((:currentTime - lastCompletedDate) / 86400000 >= periodicityDays))
          AND (lastSkippedDate IS NULL OR (:currentTime - lastSkippedDate) >= 86400000)
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomAvailableTask(durationType: String, currentTime: Long): Task?

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Query("UPDATE tasks SET lastSkippedDate = :skipTime WHERE id = :taskId")
    suspend fun setSkippedDate(taskId: String, skipTime: Long)
}