package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Routine
import com.example.data.RoutineCompletion
import com.example.data.RoutineRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class RoutineViewModel(
    private val repository: RoutineRepository,
    private val speechManager: RoutineSpeechManager? = null
) : ViewModel() {

    // Current selected date for view/checking, e.g. "2026-07-05"
    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    init {
        // Preload default routines if DB is empty
        viewModelScope.launch {
            repository.preloadDefaultRoutinesIfEmpty()
        }
    }

    // List of all routines from DB
    val allRoutines: StateFlow<List<Routine>> = repository.allRoutines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Completions for the currently selected date
    val completionsForSelectedDate: StateFlow<List<RoutineCompletion>> = _selectedDate
        .flatMapLatest { date ->
            repository.getCompletionsForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All completion logs (for streak and long-term monitoring)
    val allCompletions: StateFlow<List<RoutineCompletion>> = repository.allCompletions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derived State: Current Streak Count
    val streakCount: StateFlow<Int> = allCompletions
        .map { completions ->
            calculateStreak(completions)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun changeSelectedDate(date: LocalDate) {
        _selectedDate.value = date.toString()
    }

    fun selectPrevDay() {
        val current = LocalDate.parse(_selectedDate.value)
        _selectedDate.value = current.minusDays(1).toString()
    }

    fun selectNextDay() {
        val current = LocalDate.parse(_selectedDate.value)
        _selectedDate.value = current.plusDays(1).toString()
    }

    fun toggleRoutineCompletion(routineId: Int, date: String) {
        viewModelScope.launch {
            val dateCompletions = repository.getCompletionsForDate(date).first()
            val isCompleted = dateCompletions.any { it.routineId == routineId }
            if (isCompleted) {
                repository.uncheckRoutine(routineId, date)
            } else {
                repository.checkRoutine(routineId, date)
                // When they complete a task, say a sweet congratulations!
                val completedRoutine = allRoutines.value.find { it.id == routineId }
                if (completedRoutine != null) {
                    speakText("Awesome! You completed your ${completedRoutine.title} routine!")
                }
            }
        }
    }

    fun addRoutine(title: String, description: String, time: String, category: String, voicePhrase: String) {
        viewModelScope.launch {
            val newRoutine = Routine(
                title = title,
                description = description,
                time = time,
                category = category,
                voicePhrase = voicePhrase
            )
            repository.insertRoutine(newRoutine)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }

    // --- Voice Alerts Methods ---

    fun speakText(text: String) {
        speechManager?.speak(text)
    }

    fun stopSpeaking() {
        speechManager?.stop()
    }

    fun speakRoutineAlert(routine: Routine, isCompleted: Boolean) {
        if (isCompleted) {
            speakText("Your ${routine.title} session is already completed. Great job!")
        } else {
            val phrase = if (routine.voicePhrase.isNotBlank()) {
                routine.voicePhrase
            } else {
                "Your ${routine.title} session is there. You did not complete that!"
            }
            speakText(phrase)
        }
    }

    fun announceRemainingRoutines() {
        val currentRoutines = allRoutines.value
        val completedIds = completionsForSelectedDate.value.map { it.routineId }.toSet()
        val incompleteRoutines = currentRoutines.filter { !completedIds.contains(it.id) }

        if (incompleteRoutines.isEmpty()) {
            speakText("Outstanding! You have completed all of your daily routines for today. Keep up the perfect streak!")
        } else {
            val count = incompleteRoutines.size
            val listText = incompleteRoutines.joinToString(separator = ", and ") { routine ->
                if (routine.voicePhrase.isNotBlank()) {
                    routine.voicePhrase
                } else {
                    "your ${routine.title} is there, you did not complete that"
                }
            }
            speakText("Attention! You have $count pending routines remaining. $listText. Please take action and finish them!")
        }
    }

    // Streak Calculator
    private fun calculateStreak(completions: List<RoutineCompletion>): Int {
        if (completions.isEmpty()) return 0
        val dates = completions.map { it.date }.distinct().toSet()
        val today = LocalDate.now()
        var streak = 0
        var checkDate = today
        
        // If today has no completions, we check if yesterday had completions to keep the streak alive
        if (!dates.contains(today.toString())) {
            checkDate = today.minusDays(1)
        }
        
        while (dates.contains(checkDate.toString())) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    override fun onCleared() {
        super.onCleared()
        // Note: speechManager is lifecycle-managed by MainActivity, but we can stop speaking on clear.
        stopSpeaking()
    }
}

class RoutineViewModelFactory(
    private val repository: RoutineRepository,
    private val speechManager: RoutineSpeechManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(repository, speechManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
