package com.ltsw.animo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ltsw.animo.data.model.NotificationSettings
import com.ltsw.animo.data.model.Pet
import com.ltsw.animo.ui.components.*

@Composable
fun SettingsScreen() {
    var showManagePets by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        TopHeader("Settings")
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingsGroup(title = "General") {
                SettingsItem(title = "Manage Pets", icon = Icons.Filled.Pets) { showManagePets = true }
                SettingsItem(title = "Notifications", icon = Icons.Filled.Notifications) { showNotifications = true }
                SettingsToggleItem(
                    title = "Dark Mode",
                    icon = Icons.Filled.DarkMode,
                    checked = false, // Add logic for theme switching
                    onCheckedChange = { /* TODO */ }
                )
            }
            SettingsGroup(title = "About") {
                SettingsItem(title = "About Animo", icon = Icons.Filled.Info) { /* TODO */ }
                SettingsItem(title = "Privacy Policy", icon = Icons.Filled.Shield) { /* TODO */ }
            }
        }
    }

    if (showManagePets) {
        FullScreenSettingPage(title = "Manage Pets", onClose = { showManagePets = false }) {
            ManagePetsContent()
        }
    }
    if (showNotifications) {
        FullScreenSettingPage(title = "Notifications", onClose = { showNotifications = false }) {
            NotificationsContent()
        }
    }
}

@Composable
fun ManagePetsContent() {
    // TODO: Replace with database-backed pet data when Pet persistence is implemented
    val pets = remember {
        listOf(
            Pet(1, "Max", "Golden Retriever"),
            Pet(2, "Luna", "Siberian Husky")
        )
    }
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pets) { pet ->
            PetItem(pet = pet, onEdit = {}, onDelete = {})
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Add New Pet")
            }
        }
    }
}

@Composable
fun PetItem(pet: Pet, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(pet.name, fontWeight = FontWeight.Bold)
                Text(pet.breed, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "Delete") }
        }
    }
}

@Composable
fun NotificationsContent() {
    // TODO: Replace with database-backed notification settings when persistence is implemented
    var settings by remember { mutableStateOf(NotificationSettings(true, true, false)) }
    Column(modifier = Modifier.padding(16.dp)) {
        SettingsGroup(title = "Reminders") {
            SettingsToggleItem(title = "Appointment Reminders", icon = Icons.Outlined.CalendarToday, checked = settings.appointments) {
                settings = settings.copy(appointments = it)
            }
            SettingsToggleItem(title = "Medication Reminders", icon = Icons.Outlined.MedicalServices, checked = settings.medications) {
                settings = settings.copy(medications = it)
            }
            SettingsToggleItem(title = "Daily Summary", icon = Icons.Outlined.Summarize, checked = settings.summary) {
                settings = settings.copy(summary = it)
            }
        }
    }
}
