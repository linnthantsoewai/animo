package com.ltsw.animo.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.ltsw.animo.AnimoApplication
import com.ltsw.animo.data.model.NotificationSettings
import com.ltsw.animo.notifications.NotificationHelper
import com.ltsw.animo.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as AnimoApplication
    val notificationSettingsDao = application.database.notificationSettingsDao()
    val themePreferences = application.themePreferences

    var showNotifications by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    // Load dark mode preference from DataStore
    val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load notification settings from database
    val notificationSettings by notificationSettingsDao.getSettings()
        .collectAsState(initial = NotificationSettings(appointments = true, medications = true, summary = false))

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        scope.launch {
            if (isGranted) {
                snackbarHostState.showSnackbar("Notification permission granted")
            } else {
                snackbarHostState.showSnackbar("Notification permission denied")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            TopHeader("Settings")
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                SettingsGroup(title = "Preferences") {
                    SettingsItem(
                        title = "Notifications",
                        icon = Icons.Filled.Notifications
                    ) {
                        // Check permission first
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!NotificationHelper.hasNotificationPermission(context)) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        showNotifications = true
                    }
                    SettingsToggleItem(
                        title = "Dark Mode",
                        icon = Icons.Filled.DarkMode,
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                themePreferences.setDarkMode(enabled)
                                snackbarHostState.showSnackbar(
                                    message = "Dark mode ${if (enabled) "enabled" else "disabled"}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    }
                }

            item {
                SettingsGroup(title = "App") {
                    SettingsItem(
                        title = "About Animo",
                        icon = Icons.Filled.Info
                    ) {
                        showAbout = true
                    }
                    SettingsItem(
                        title = "Privacy Policy",
                        icon = Icons.Filled.Shield
                    ) {
                        showPrivacyPolicy = true
                    }
                    val context = LocalContext.current
                    SettingsItem(
                        title = "Rate Us",
                        icon = Icons.Filled.Star
                    ) {
                        // Try to open app store, fallback to browser
                        try {
                            val appPackageName = context.packageName
                            val intent = Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri())
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            // Fallback to browser if Play Store not available
                            val appPackageName = context.packageName
                            val intent = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$appPackageName".toUri())
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                // Silently fail if neither works
                            }
                        }
                    }
                }
            }

            item {
                SettingsGroup(title = "Support") {
                    val context = LocalContext.current
                    SettingsItem(
                        title = "Help & FAQ",
                        icon = Icons.AutoMirrored.Filled.Help
                    ) {
                        // Open help page with error handling
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, "https://github.com/linnthantsoewai/animo".toUri())
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            // Silently fail if no browser available
                        }
                    }
                    SettingsItem(
                        title = "Contact Us",
                        icon = Icons.Filled.Email
                    ) {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:support@animo.app".toUri()
                                putExtra(Intent.EXTRA_SUBJECT, "Animo App Support")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            // Silently fail if no email app available
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Animo v1.0.0\nMade with ❤️ for pet lovers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    // Notifications Settings Page
    if (showNotifications) {
        FullScreenSettingPage(
            title = "Notifications",
            onClose = { showNotifications = false }
        ) {
            NotificationsContent(
                settings = notificationSettings ?: NotificationSettings(appointments = true, medications = true, summary = false),
                onSettingsChange = { newSettings ->
                    scope.launch {
                        notificationSettingsDao.insertSettings(newSettings)

                        // Schedule or cancel daily summary
                        NotificationHelper.scheduleDailySummary(context, newSettings.summary)
                    }
                }
            )
        }
    }

    // About Page
    if (showAbout) {
        FullScreenSettingPage(
            title = "About Animo",
            onClose = { showAbout = false }
        ) {
            AboutContent()
        }
    }

    // Privacy Policy Page
    if (showPrivacyPolicy) {
        FullScreenSettingPage(
            title = "Privacy Policy",
            onClose = { showPrivacyPolicy = false }
        ) {
            PrivacyPolicyContent()
        }
    }
}

@Composable
fun NotificationsContent(
    settings: NotificationSettings,
    onSettingsChange: (NotificationSettings) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val hasPermission = NotificationHelper.hasNotificationPermission(context)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Manage your notification preferences",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Permission status card
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Notification permission not granted. Please enable notifications in app settings.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            SettingsGroup(title = "Reminders") {
                SettingsToggleItem(
                    title = "Appointment Reminders",
                    icon = Icons.Outlined.CalendarToday,
                    checked = settings.appointments
                ) {
                    onSettingsChange(settings.copy(appointments = it))
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (it) "Appointment reminders enabled" else "Appointment reminders disabled",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsToggleItem(
                    title = "Medication Reminders",
                    icon = Icons.Outlined.MedicalServices,
                    checked = settings.medications
                ) {
                    onSettingsChange(settings.copy(medications = it))
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (it) "Medication reminders enabled" else "Medication reminders disabled",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsToggleItem(
                    title = "Daily Summary",
                    icon = Icons.Outlined.Summarize,
                    checked = settings.summary
                ) {
                    onSettingsChange(settings.copy(summary = it))
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (it) "Daily summary enabled (8 PM)" else "Daily summary disabled",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "How notifications work",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Appointments & Medications: 1 hour before scheduled time\n• Daily Summary: Every day at 8 PM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AboutContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Filled.Pets,
            contentDescription = "Animo Logo",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Animo",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your complete pet care companion",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FeatureItem("📅 Activity scheduling")
                FeatureItem("💉 Vaccination tracking")
                FeatureItem("💊 Medication reminders")
                FeatureItem("🐾 Multiple pet profiles")
                FeatureItem("📊 Activity insights")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Made with ❤️ for pet lovers everywhere",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = "© 2025 Animo. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PrivacyPolicyContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Last updated: November 20, 2025",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            PolicySection(
                title = "Data Collection",
                content = "Animo stores all your pet data locally on your device. We do not collect, transmit, or store any personal information on external servers."
            )
        }

        item {
            PolicySection(
                title = "Data Storage",
                content = "All pet profiles, activities, and schedules are stored securely in your device's local database. Your data remains private and under your control."
            )
        }

        item {
            PolicySection(
                title = "Permissions",
                content = "Animo may request notifications permission to send you reminders about your pet's activities. You can manage these permissions in your device settings."
            )
        }

        item {
            PolicySection(
                title = "Data Sharing",
                content = "We do not share your data with any third parties. Your pet information stays on your device."
            )
        }

        item {
            PolicySection(
                title = "Data Security",
                content = "Your data is protected by your device's security features. We recommend keeping your device secure with a password or biometric lock."
            )
        }

        item {
            PolicySection(
                title = "Contact",
                content = "If you have any questions about this privacy policy, please contact us at support@animo.app"
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}

