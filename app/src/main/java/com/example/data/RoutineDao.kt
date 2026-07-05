package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY time ASC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE isActive = 1 ORDER BY time ASC")
    fun getActiveRoutines(): Flow<List<Routine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Update
    suspend fun updateRoutine(routine: Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("SELECT * FROM routine_completions ORDER BY timestamp DESC")
    fun getAllCompletions(): Flow<List<RoutineCompletion>>

    @Query("SELECT * FROM routine_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<RoutineCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: RoutineCompletion)

    @Query("DELETE FROM routine_completions WHERE routineId = :routineId AND date = :date")
    suspend fun deleteCompletion(routineId: Int, date: String)

    @Query("DELETE FROM routine_completions WHERE routineId = :routineId")
    suspend fun deleteCompletionsForRoutine(routineId: Int)
}
