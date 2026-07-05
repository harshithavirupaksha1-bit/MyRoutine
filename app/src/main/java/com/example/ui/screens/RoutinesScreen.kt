package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Routine
import com.example.ui.RoutineViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel,
    innerPadding: PaddingValues
) {
    val selectedDateStr by viewModel.selectedDate.collectAsState()
    val routines by viewModel.allRoutines.collectAsState()
    val completions by viewModel.completionsForSelectedDate.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()

    val selectedDate = LocalDate.parse(selectedDateStr)
    val today = LocalDate.now()
    
    val formattedDate = remember(selectedDate) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
        selectedDate.format(formatter)
    }

    val isToday = remember(selectedDate) {
        selectedDate.isEqual(today)
    }

    val completedIds = remember(completions) {
        completions.map { it.routineId }.toSet()
    }

    val totalCount = routines.size
    val completedCount = routines.count { completedIds.contains(it.id) }
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header greeting ---
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (isToday) "Good morning, Alex" else "Historic Log",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Let's track and check off your healthy routines.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- 1. Top Date Navigation bar ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.selectPrevDay() },
                    modifier = Modifier
                        .testTag("prev_date_button")
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isToday) "TODAY" else selectedDate.dayOfWeek.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = { viewModel.selectNextDay() },
                    modifier = Modifier
                        .testTag("next_date_button")
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // --- 2. Streak, Completion Progress & Voice Broadcast Board ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Welcome/Streak details
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Streak",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "$streakCount Days Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Text(
                                text = "Your daily habits are your strength.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Completion Progress Text
                            Text(
                                text = "$completedCount of $totalCount completed",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Circular Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(64.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 5.dp,
                                trackColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice announcement quick alert card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { viewModel.announceRemainingRoutines() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Voice Broadcast Alert",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Voice Assistant",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Speak uncompleted routine reminders aloud",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Speak Now",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // --- Section Title ---
        item {
            Text(
                text = "Today's Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // --- 3. Routines List ---
        if (totalCount == 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = "No routines",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Your daily schedule is blank.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Head to the 'Manage' tab to set up routines.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(routines, key = { it.id }) { routine ->
                val isCompleted = completedIds.contains(routine.id)
                RoutineItemRow(
                    routine = routine,
                    isCompleted = isCompleted,
                    onToggle = { viewModel.toggleRoutineCompletion(routine.id, selectedDateStr) },
                    onSpeak = { viewModel.speakRoutineAlert(routine, isCompleted) }
                )
            }
        }
    }
}

@Composable
fun RoutineItemRow(
    routine: Routine,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onSpeak: () -> Unit
) {
    val categoryIcon = when (routine.category.lowercase()) {
        "gym" -> Icons.Default.FitnessCenter
        "food" -> Icons.Default.Restaurant
        "work" -> Icons.Default.Work
        "health" -> Icons.Default.Favorite
        "mind" -> Icons.Default.Spa
        else -> Icons.Default.Star
    }

    // Natural tonal theme color choices
    val iconContainerColor = when (routine.category.lowercase()) {
        "gym" -> Color(0xFFF2F1E9)
        "food" -> Color(0xFFEBE9DE)
        "work" -> Color(0xFFECEBE4)
        "health" -> Color(0xFFFBF9F1)
        "mind" -> Color(0xFFE2E3D8)
        else -> Color(0xFFECEBE4)
    }

    val iconColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.id}")
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(20.dp)
            )
            .clickable { onToggle() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Icon Block
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconContainerColor)
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = routine.category,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Routine Title, Time & Custom Phrase info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (routine.description.isNotEmpty()) {
                    Text(
                        text = routine.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (isCompleted) 0.5f else 0.8f
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Scheduled Time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = routine.time,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Custom Spoken Reminder Sound Button
            IconButton(
                onClick = { onSpeak() },
                modifier = Modifier
                    .testTag("speak_button_${routine.id}")
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak Routine Reminder Alarm",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Click Right Checkmark Action
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("check_button_${routine.id}")
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    )
                    .border(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .clickable { onToggle() }
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
