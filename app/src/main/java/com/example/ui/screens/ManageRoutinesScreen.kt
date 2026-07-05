package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Routine
import com.example.ui.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRoutinesScreen(
    viewModel: RoutineViewModel,
    innerPadding: PaddingValues
) {
    val routines by viewModel.allRoutines.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Routine?>(null) }

    // State form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeHour by remember { mutableStateOf("07") }
    var timeMin by remember { mutableStateOf("00") }
    var timeAmPm by remember { mutableStateOf("AM") }
    var selectedCategory by remember { mutableStateOf("Gym") }
    var voicePhrase by remember { mutableStateOf("") }

    val categories = listOf("Gym", "Food", "Work", "Health", "Mind", "Other")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    title = ""
                    description = ""
                    timeHour = "07"
                    timeMin = "00"
                    timeAmPm = "AM"
                    selectedCategory = "Gym"
                    voicePhrase = ""
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_routine_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Routine")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Manage Schedule",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Add custom voice alarm phrases for your daily routines",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (routines.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddTask,
                                contentDescription = "Empty",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "Create a custom routine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Click the '+' button below to configure a new habit.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(routines, key = { it.id }) { routine ->
                    val categoryIcon = when (routine.category.lowercase()) {
                        "gym" -> Icons.Default.FitnessCenter
                        "food" -> Icons.Default.Restaurant
                        "work" -> Icons.Default.Work
                        "health" -> Icons.Default.Favorite
                        "mind" -> Icons.Default.Spa
                        else -> Icons.Default.Star
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manage_card_${routine.id}")
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = routine.category,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = routine.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (routine.description.isNotEmpty()) {
                                    Text(
                                        text = routine.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (routine.voicePhrase.isNotBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VoiceChat,
                                            contentDescription = "Voice Phrase Active",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = routine.voicePhrase,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Text(
                                    text = routine.time,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = { showDeleteDialog = routine },
                                modifier = Modifier.testTag("delete_routine_button_${routine.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Routine",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Add Routine Dialog ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(text = "New Daily Routine", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title (e.g. Morning Gym)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_routine_title_field")
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Short Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_routine_desc_field")
                        )

                        OutlinedTextField(
                            value = voicePhrase,
                            onValueChange = { voicePhrase = it },
                            label = { Text("Custom voice reminder phrase") },
                            placeholder = { Text("e.g. Your workout session is there. You did not complete that!") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_routine_voice_field")
                        )

                        // Custom Simplified Time Selector
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Scheduled Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hour
                                OutlinedTextField(
                                    value = timeHour,
                                    onValueChange = { if (it.length <= 2) timeHour = it },
                                    label = { Text("HH") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("time_hour")
                                )
                                Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                // Min
                                OutlinedTextField(
                                    value = timeMin,
                                    onValueChange = { if (it.length <= 2) timeMin = it },
                                    label = { Text("MM") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("time_min")
                                )
                                // AM / PM Toggle
                                Button(
                                    onClick = { timeAmPm = if (timeAmPm == "AM") "PM" else "AM" },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("time_ampm_btn")
                                ) {
                                    Text(timeAmPm)
                                }
                            }
                        }

                        // Category dropdown / selector row
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val chunked = categories.chunked(3)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    chunked.forEach { chunk ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            chunk.forEach { cat ->
                                                val isSelected = selectedCategory == cat
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(vertical = 4.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                                        )
                                                        .clickable { selectedCategory = cat }
                                                        .padding(8.dp)
                                                ) {
                                                    Text(
                                                        text = cat,
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                val hr = timeHour.padStart(2, '0')
                                val mn = timeMin.padStart(2, '0')
                                val finalTime = "$hr:$mn $timeAmPm"
                                val finalPhrase = if (voicePhrase.isNotBlank()) voicePhrase else "Your $title session is there. You did not complete that!"
                                viewModel.addRoutine(title, description, finalTime, selectedCategory, finalPhrase)
                                showAddDialog = false
                            }
                        },
                        modifier = Modifier.testTag("confirm_add_button")
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // --- Delete Confirmation Dialog ---
        if (showDeleteDialog != null) {
            val routine = showDeleteDialog!!
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text(text = "Delete Routine?") },
                text = { Text("Are you sure you want to delete '${routine.title}'? This will also clear all historic completions associated with it.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteRoutine(routine)
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
