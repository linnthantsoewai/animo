package com.ltsw.animo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import com.ltsw.animo.ui.viewmodel.ActivityViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(viewModel: ActivityViewModel) {
    val activities by viewModel.allActivities.collectAsState()
    val sortedActivities = activities.sortedBy { it.dateTime }
    val groupedActivities = sortedActivities.groupBy { it.dateTime.toLocalDate() }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        TopHeader("Schedule")
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedActivities.forEach { (date, activitiesOnDate) ->
                item {
                    Text(
                        date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(activitiesOnDate) { activity ->
                    ActivityCard(
                        activity = activity,
                        onDelete = { viewModel.deleteById(activity.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun ActivityCard(activity: Activity, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val activityInfo = activity.type.getInfo()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(activityInfo.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(activityInfo.icon, contentDescription = activity.type.name, tint = activityInfo.color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(activity.title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    activity.dateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Options", tint = Color.Gray)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    )
                }
            }
        }
    }
}

private data class ActivityInfo(val icon: ImageVector, val color: Color)

private fun ActivityType.getInfo(): ActivityInfo {
    return when (this) {
        ActivityType.WALK -> ActivityInfo(Icons.Outlined.Pets, Color.Green)
        ActivityType.MEAL -> ActivityInfo(Icons.Outlined.Restaurant, Color(0xFFFFA500))
        ActivityType.MEDICATION -> ActivityInfo(Icons.Outlined.MedicalServices, Color.Red)
        ActivityType.APPOINTMENT -> ActivityInfo(Icons.Outlined.Event, Color.Blue)
        ActivityType.VACCINATION -> ActivityInfo(Icons.Outlined.Vaccines, Color(0xFF800080))
    }
}