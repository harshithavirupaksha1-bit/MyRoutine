package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Routine
import com.example.data.RoutineCompletion
import com.example.ui.RoutineViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MonitorScreen(
    viewModel: RoutineViewModel,
    innerPadding: PaddingValues
) {
    val routines by viewModel.allRoutines.collectAsState()
    val allCompletions by viewModel.allCompletions.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()

    // Map routine ID to Routine for rich history displays
    val routineMap = remember(routines) {
        routines.associateBy { it.id }
    }

    // Group completion records by date, sorted descending
    val groupedHistory = remember(allCompletions, routineMap) {
        allCompletions
            .mapNotNull { completion ->
                val routine = routineMap[completion.routineId]
                if (routine != null) {
                    completion to routine
                } else null
            }
            .groupBy { it.first.date }
            .toList()
            .sortedByDescending { it.first }
    }

    val totalCompletionsCount = allCompletions.size

    val categoryMetrics = remember(routines, allCompletions) {
        val countMap = mutableMapOf<String, Int>()
        val totalMap = mutableMapOf<String, Int>()
        
        routines.forEach { r ->
            totalMap[r.category] = (totalMap[r.category] ?: 0) + 1
        }
        
        allCompletions.forEach { completion ->
            val routine = routineMap[completion.routineId]
            if (routine != null) {
                countMap[routine.category] = (countMap[routine.category] ?: 0) + 1
            }
        }
        
        totalMap.keys.map { category ->
            CategoryMetric(
                category = category,
                completedCount = countMap[category] ?: 0,
                routineCount = totalMap[category] ?: 0
            )
        }.sortedByDescending { it.completedCount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header / Title ---
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Activity Monitor",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Track your routine completion history and metrics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- Stats Dashboard Cards ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "$streakCount Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Current Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Completions Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed Total",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "$totalCompletionsCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total Checkoffs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- Category Breakdown Segment ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (categoryMetrics.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "Create routines to see category statistics.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            categoryMetrics.forEach { metric ->
                                val categoryIcon = when (metric.category.lowercase()) {
                                    "gym" -> Icons.Default.FitnessCenter
                                    "food" -> Icons.Default.Restaurant
                                    "work" -> Icons.Default.Work
                                    "health" -> Icons.Default.Favorite
                                    "mind" -> Icons.Default.Spa
                                    else -> Icons.Default.Star
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = categoryIcon,
                                        contentDescription = metric.category,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = metric.category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(64.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    val progress = if (metric.routineCount > 0) metric.completedCount.toFloat() / (metric.completedCount + metric.routineCount) else 0f
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.secondaryContainer
                                    )

                                    Text(
                                        text = "${metric.completedCount} completed",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- History Timeline Segment ---
        item {
            Text(
                text = "History Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (groupedHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No routines have been checked off yet. Your completed routines logs will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(groupedHistory, key = { it.first }) { (dateStr, list) ->
                val dateParsed = LocalDate.parse(dateStr)
                val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
                val displayDate = dateParsed.format(formatter)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_day_block_$dateStr")
                        .padding(bottom = 8.dp)
                ) {
                    // Date Header
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )

                    // Logs card list
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            list.forEach { (_, routine) ->
                                val categoryIcon = when (routine.category.lowercase()) {
                                    "gym" -> Icons.Default.FitnessCenter
                                    "food" -> Icons.Default.Restaurant
                                    "work" -> Icons.Default.Work
                                    "health" -> Icons.Default.Favorite
                                    "mind" -> Icons.Default.Spa
                                    else -> Icons.Default.Star
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.background),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = categoryIcon,
                                            contentDescription = routine.category,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = routine.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Completed at ${routine.time}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Check",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CategoryMetric(
    val category: String,
    val completedCount: Int,
    val routineCount: Int
)

