package com.ltsw.animo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
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
import java.time.LocalDateTime

@Composable
fun DashboardScreen(viewModel: ActivityViewModel) {
    val activities by viewModel.allActivities.collectAsState()
    val today = LocalDateTime.now().toLocalDate()
    val todayActivities = activities.filter { it.dateTime.toLocalDate() == today }

    val walksToday = todayActivities.count { it.type == ActivityType.WALK }
    val mealsToday = todayActivities.count { it.type == ActivityType.MEAL }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Header("Good morning, Alex", "For Max 🐾") }
        item { NextAppointmentCard() }
        item {
            Text("Today's Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryGrid(walksToday, mealsToday)
        }
        item {
            Text("Quick Log", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            QuickLogButtons(viewModel)
        }
    }
}

@Composable
private fun Header(subtitle: String, title: String) {
    Column {
        Text(subtitle, color = Color.Gray)
        Text(title, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun NextAppointmentCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Next Appointment", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Annual Check-up", color = Color.White.copy(alpha = 0.8f))
                Text("Dr. Reynolds", color = Color.White.copy(alpha = 0.8f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Oct 25", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("10:00 AM", color = Color.White)
            }
        }
    }
}

@Composable
private fun SummaryGrid(walksToday: Int, mealsToday: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(icon = Icons.Outlined.Pets, label = "Walks", value = "$walksToday", color = Color.Green, modifier = Modifier.weight(1f))
        SummaryCard(icon = Icons.Outlined.Restaurant, label = "Meals", value = "$mealsToday / 2", color = Color(0xFFFFA500), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(icon: ImageVector, label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(label, color = Color.Gray)
        }
    }
}

@Composable
private fun QuickLogButtons(viewModel: ActivityViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickLogButton(
            label = "Walk",
            icon = Icons.Outlined.Pets,
            color = Color.Green,
            modifier = Modifier.weight(1f),
            onClick = {
                val activity = Activity(
                    type = ActivityType.WALK,
                    title = "Quick Walk",
                    dateTime = LocalDateTime.now()
                )
                viewModel.insert(activity)
            }
        )
        QuickLogButton(
            label = "Meal",
            icon = Icons.Outlined.Restaurant,
            color = Color(0xFFFFA500),
            modifier = Modifier.weight(1f),
            onClick = {
                val activity = Activity(
                    type = ActivityType.MEAL,
                    title = "Meal",
                    dateTime = LocalDateTime.now()
                )
                viewModel.insert(activity)
            }
        )
        QuickLogButton(
            label = "Meds",
            icon = Icons.Outlined.MedicalServices,
            color = Color.Red,
            modifier = Modifier.weight(1f),
            onClick = {
                val activity = Activity(
                    type = ActivityType.MEDICATION,
                    title = "Medication",
                    dateTime = LocalDateTime.now()
                )
                viewModel.insert(activity)
            }
        )
    }
}

@Composable
private fun QuickLogButton(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}