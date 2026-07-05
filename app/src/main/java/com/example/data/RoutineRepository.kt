package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class RoutineRepository(private val routineDao: RoutineDao) {
    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines()
    val activeRoutines: Flow<List<Routine>> = routineDao.getActiveRoutines()
    val allCompletions: Flow<List<RoutineCompletion>> = routineDao.getAllCompletions()

    fun getCompletionsForDate(date: String): Flow<List<RoutineCompletion>> =
        routineDao.getCompletionsForDate(date)

    suspend fun insertRoutine(routine: Routine) {
        routineDao.insertRoutine(routine)
    }

    suspend fun updateRoutine(routine: Routine) {
        routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        // Delete completions for this routine first
        routineDao.deleteCompletionsForRoutine(routine.id)
        routineDao.deleteRoutine(routine)
    }

    suspend fun checkRoutine(routineId: Int, date: String) {
        routineDao.insertCompletion(RoutineCompletion(routineId = routineId, date = date))
    }

    suspend fun uncheckRoutine(routineId: Int, date: String) {
        routineDao.deleteCompletion(routineId, date)
    }

    suspend fun preloadDefaultRoutinesIfEmpty() {
        val currentRoutines = allRoutines.first()
        if (currentRoutines.isEmpty()) {
            val defaults = listOf(
                Routine(
                    title = "Morning Gym",
                    description = "Cardio, lifting, or simple stretching to kickstart your body.",
                    time = "07:00 AM",
                    category = "Gym"
                ),
                Routine(
                    title = "Healthy Breakfast",
                    description = "Oatmeal, fruits, or eggs with a cup of green tea.",
                    time = "08:15 AM",
                    category = "Food"
                ),
                Routine(
                    title = "Deep Focus Session",
                    description = "Put away your phone and work/study uninterrupted for 45 mins.",
                    time = "09:30 AM",
                    category = "Work"
                ),
                Routine(
                    title = "Lunch & Walk",
                    description = "Nutritious balanced meal followed by a light 10-minute walk.",
                    time = "01:00 PM",
                    category = "Food"
                ),
                Routine(
                    title = "Afternoon Recharge",
                    description = "Quick mindfulness breathing, hydration check, or tea break.",
                    time = "03:30 PM",
                    category = "Mind"
                ),
                Routine(
                    title = "Daily Learning",
                    description = "Read 10 pages of a book or watch an educational tutorial.",
                    time = "05:00 PM",
                    category = "Work"
                ),
                Routine(
                    title = "Light Dinner",
                    description = "A healthy, light evening meal before 8:00 PM.",
                    time = "07:30 PM",
                    category = "Food"
                ),
                Routine(
                    title = "Evening Reflection",
                    description = "Reflect on your day, plan tomorrow, and unplug from screens.",
                    time = "09:45 PM",
                    category = "Mind"
                )
            )
            for (routine in defaults) {
                routineDao.insertRoutine(routine)
            }
        }
    }
}
