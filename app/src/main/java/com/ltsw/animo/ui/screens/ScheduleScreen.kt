package com.example.animo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
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
import com.example.animo.data.SampleData
import com.example.animo.data.model.Activity
import com.example.animo.data.model.ActivityType
import com.example.animo.ui.components.TopHeader
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen() {
    val activities = SampleData.activities.sortedBy { it.dateTime }
    val groupedActivities = activities.groupBy { it.dateTime.toLocalDate() }

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
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(activitiesOnDate) { activity ->
                    ActivityCard(activity)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
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
                Text(activity.title, fontWeight = FontWeight.SemiBold)
                Text(
                    activity.dateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = { /* TODO: Edit/Delete */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Options")
            }
        }
    }
}


// Helper data class and extension function for styling activity types
data class ActivityInfo(val icon: ImageVector, val color: Color)

fun ActivityType.getInfo(): ActivityInfo {
    return when (this) {
        ActivityType.WALK -> ActivityInfo(Icons.Outlined.Pets, Color.Green)
        ActivityType.MEAL -> ActivityInfo(Icons.Outlined.Restaurant, Color(0xFFFFA500))
        ActivityType.MEDICATION -> ActivityInfo(Icons.Outlined.MedicalServices, Color.Red)
        ActivityType.APPOINTMENT -> ActivityInfo(Icons.Outlined.Event, Color.Blue)
        ActivityType.VACCINATION -> ActivityInfo(Icons.Outlined.Vaccines, Color(0xFF800080))
    }
}
