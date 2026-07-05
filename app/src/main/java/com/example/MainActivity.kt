package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.RoutineRepository
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.RoutineSpeechManager
import com.example.ui.RoutineViewModel
import com.example.ui.RoutineViewModelFactory
import com.example.ui.screens.ManageRoutinesScreen
import com.example.ui.screens.MonitorScreen
import com.example.ui.screens.RoutinesScreen

class MainActivity : ComponentActivity() {
    private lateinit var speechManager: RoutineSpeechManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize TTS Speech Manager
        speechManager = RoutineSpeechManager(applicationContext)

        // Initialize Room Database, DAO and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RoutineRepository(database.routineDao())
        
        // Instantiate the ViewModel with our Speech Manager
        val viewModel = ViewModelProvider(
            this, 
            RoutineViewModelFactory(repository, speechManager)
        )[RoutineViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: RoutineViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Daily Routines",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Global voice assistance checkoff trigger in top bar!
                    IconButton(
                        onClick = { viewModel.announceRemainingRoutines() },
                        modifier = Modifier
                            .testTag("voice_assistant_app_bar")
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Read Incomplete Routines Aloud",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Today's Routines"
                        )
                    },
                    label = { Text("Today") },
                    modifier = Modifier.testTag("tab_today"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Monitor Progress"
                        )
                    },
                    label = { Text("Monitor") },
                    modifier = Modifier.testTag("tab_monitor"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "Manage Routines"
                        )
                    },
                    label = { Text("Manage") },
                    modifier = Modifier.testTag("tab_manage"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> RoutinesScreen(viewModel = viewModel, innerPadding = innerPadding)
                1 -> MonitorScreen(viewModel = viewModel, innerPadding = innerPadding)
                2 -> ManageRoutinesScreen(viewModel = viewModel, innerPadding = innerPadding)
            }
        }
    }
}
