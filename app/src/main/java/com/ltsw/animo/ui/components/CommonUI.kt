package com.ltsw.animo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// A reusable header for main screens
@Composable
fun TopHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 32.sp)
    }
}

// A full-screen container for settings sub-pages
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenSettingPage(title: String, onClose: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            content()
        }
    }
}

// A reusable group container for settings items
@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title.uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column {
                content()
            }
        }
    }
}

// A clickable settings item that navigates to another screen
@Composable
fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = "Go", tint = Color.Gray)
    }
}

// A settings item with a toggle switch
@Composable
fun SettingsToggleItem(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            modifier = Modifier.weight(1f),
            color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
            )
        )
    }
}
