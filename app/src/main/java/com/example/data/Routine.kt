package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val time: String, // e.g. "07:00 AM" or "13:00"
    val category: String, // e.g. "Gym", "Food", "Work", "Health", "Mind", "Other"
    val daysOfWeek: String = "Mon,Tue,Wed,Thu,Fri,Sat,Sun", // Comma separated days
    val isActive: Boolean = true,
    val voicePhrase: String = "" // Custom speech notification/alert phrase
)
