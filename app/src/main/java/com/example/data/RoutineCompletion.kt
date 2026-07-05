package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_completions")
data class RoutineCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val date: String, // formatted as "YYYY-MM-DD" for easy matching
    val timestamp: Long = System.currentTimeMillis()
)
