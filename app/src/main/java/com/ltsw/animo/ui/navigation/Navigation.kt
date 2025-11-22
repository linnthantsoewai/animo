package com.ltsw.animo.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.ltsw.animo.AnimoApplication
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import com.ltsw.animo.notifications.NotificationHelper
import com.ltsw.animo.ui.screens.DashboardScreen
import com.ltsw.animo.ui.screens.ProfileScreen
import com.ltsw.animo.ui.screens.ScheduleScreen
import com.ltsw.animo.ui.screens.SettingsScreen
import com.ltsw.animo.ui.viewmodel.ActivityViewModel
import com.ltsw.animo.ui.viewmodel.ActivityViewModelFactory
import com.ltsw.animo.ui.viewmodel.PetViewModel
import com.ltsw.animo.ui.viewmodel.PetViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// --- Screen Definitions ---
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    object Schedule : Screen("schedule", "Schedule", Icons.Filled.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Filled.Star)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

// --- Main App Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimoApp(
    isDarkMode: Boolean,
    isDynamicColorEnabled: Boolean
) {
    val context = LocalContext.current
    val application = context.applicationContext as AnimoApplication
    val viewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModelFactory(application.activityRepository)
    )
    val petViewModel: PetViewModel = viewModel(
        factory = PetViewModelFactory(application.petRepository)
    )

    // Sync selected pet from PetViewModel to ActivityViewModel
    val selectedPet by petViewModel.selectedPet.collectAsState()
    LaunchedEffect(selectedPet) {
        viewModel.setSelectedPet(selectedPet?.id)
    }

    val navController = rememberNavController()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                onAddClick = { showDialog = true }
            )
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = snap()) },
            exitTransition = { fadeOut(animationSpec = snap()) },
            popEnterTransition = { fadeIn(animationSpec = snap()) },
            popExitTransition = { fadeOut(animationSpec = snap()) }
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel, petViewModel) }
            composable(Screen.Schedule.route) { ScheduleScreen(viewModel, petViewModel) }
            composable(Screen.Profile.route) { ProfileScreen(petViewModel) }
            composable(Screen.Settings.route) { 
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    isDynamicColorEnabled = isDynamicColorEnabled
                ) 
            }
        }
    }

    if (showDialog) {
        // Only show dialog if a pet is selected
        selectedPet?.let { pet ->
            AddActivityDialog(
                petId = pet.id,
                onDismiss = { showDialog = false },
                onSave = { newActivity ->
                    viewModel.insert(newActivity)

                    // Schedule notification if it's an appointment, vaccination, or medication
                    if (newActivity.type in listOf(
                            ActivityType.APPOINTMENT,
                            ActivityType.VACCINATION,
                            ActivityType.MEDICATION
                        )
                    ) {
                        NotificationHelper.scheduleActivityNotification(context, newActivity)
                    }

                    showDialog = false
                }
            )
        } ?: run {
            // Show a message to select a pet first
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("No Pet Selected") },
                text = { Text("Please add and select a pet from the Profile page before adding activities.") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, onAddClick: () -> Unit) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Schedule,
        Screen.Profile,
        Screen.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(120.dp)
    ) {
        // First two items
        items.take(2).forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray
                )
            )
        }

        // Center Add Button with blue background
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Activity",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            label = { Text("Add") },
            selected = false,
            onClick = onAddClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = Color.Transparent
            )
        )

        // Last two items
        items.takeLast(2).forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}


// --- Add Activity Dialog ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivityDialog(petId: Long, onDismiss: () -> Unit, onSave: (Activity) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var activityType by rememberSaveable { mutableStateOf(ActivityType.WALK) }
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var selectedTime by rememberSaveable { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Log an Activity", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = activityType.name.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        ActivityType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    activityType = type
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedDate.format(dateFormatter),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Date") },
                            trailingIcon = { Icon(Icons.Default.DateRange, "Date Picker")},
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedTime.format(timeFormatter),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time") },
                            trailingIcon = { Icon(Icons.Filled.DateRange, "Time Picker")},
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showTimePicker = true })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val newActivity = Activity(
                            id = 0,  // Let Room auto-generate the ID
                            petId = petId,  // Associate with current pet
                            type = activityType,
                            title = title,
                            dateTime = LocalDateTime.of(selectedDate, selectedTime)
                        )
                        onSave(newActivity)
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        showDatePicker = false
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = false
        )
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                showTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun TimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    content()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}
