package com.ltsw.animo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltsw.animo.AnimoApplication
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import com.ltsw.animo.ui.viewmodel.ActivityViewModel
import com.ltsw.animo.ui.viewmodel.PetViewModel
import com.ltsw.animo.ui.theme.ActivityColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(viewModel: ActivityViewModel, petViewModel: PetViewModel) {
    val context = LocalContext.current
    val application = context.applicationContext as AnimoApplication
    val userRepository = application.userRepository

    // Get logged-in user
    val loggedInUser by userRepository.loggedInUser.collectAsState(initial = null)

    // Get selected pet
    val selectedPet by petViewModel.selectedPet.collectAsState()

    val activities by viewModel.allActivities.collectAsState()
    val today = LocalDateTime.now().toLocalDate()
    val todayActivities = activities.filter { it.dateTime.toLocalDate() == today }

    // Filter upcoming important activities (appointments, vaccinations, medications)
    val now = LocalDateTime.now()
    val upcomingImportantActivities = activities.filter {
        it.dateTime.isAfter(now) &&
        (it.type == ActivityType.APPOINTMENT ||
         it.type == ActivityType.VACCINATION ||
         it.type == ActivityType.MEDICATION)
    }.sortedBy { it.dateTime }.take(5) // Show max 5 upcoming activities

    val walksToday = todayActivities.count { it.type == ActivityType.WALK }
    val mealsToday = todayActivities.count { it.type == ActivityType.MEAL }

    // Create greeting message
    val userName = loggedInUser?.name ?: "User"
    val petName = selectedPet?.name ?: "your pet"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Header("Good morning, $userName", "For $petName 🐾") }

        // Show sliding carousel only if there are upcoming activities
        if (upcomingImportantActivities.isNotEmpty()) {
            item { UpcomingActivitiesCarousel(upcomingImportantActivities) }
        }

        item {
            Text("Today's Summary", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryGrid(walksToday, mealsToday)
        }
        item {
            Text("Quick Log", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            QuickLogButtons(viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UpcomingActivitiesCarousel(activities: List<Activity>) {
    val pagerState = rememberPagerState(pageCount = { activities.size })
    val coroutineScope = rememberCoroutineScope()

    // Auto-slide effect
    LaunchedEffect(pagerState.currentPage) {
        if (activities.size > 1) {
            delay(5000) // Wait 5 seconds
            val nextPage = (pagerState.currentPage + 1) % activities.size
            coroutineScope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            ActivityCard(activities[page])
        }

        // Page indicators
        if (activities.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(activities.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                    if (index < activities.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: Activity) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val (cardColor, icon, label) = when (activity.type) {
        ActivityType.APPOINTMENT -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Outlined.CalendarToday,
            "Next Appointment"
        )
        ActivityType.VACCINATION -> Triple(
            ActivityColors.Vaccination,
            Icons.Outlined.Vaccines,
            "Upcoming Vaccination"
        )
        ActivityType.MEDICATION -> Triple(
            ActivityColors.Medication,
            Icons.Outlined.MedicalServices,
            "Medication Reminder"
        )
        else -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Outlined.CalendarToday,
            "Upcoming Activity"
        )
    }

    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    activity.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    activity.dateTime.format(dateFormatter),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    activity.dateTime.format(timeFormatter),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun Header(subtitle: String, title: String) {
    Column {
        Text(subtitle, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
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
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
    val selectedPetId by viewModel.selectedPetId.collectAsState()

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
                selectedPetId?.let { petId ->
                    val activity = Activity(
                        petId = petId,
                        type = ActivityType.WALK,
                        title = "Quick Walk",
                        dateTime = LocalDateTime.now()
                    )
                    viewModel.insert(activity)
                }
            }
        )
        QuickLogButton(
            label = "Meal",
            icon = Icons.Outlined.Restaurant,
            color = Color(0xFFFFA500),
            modifier = Modifier.weight(1f),
            onClick = {
                selectedPetId?.let { petId ->
                    val activity = Activity(
                        petId = petId,
                        type = ActivityType.MEAL,
                        title = "Meal",
                        dateTime = LocalDateTime.now()
                    )
                    viewModel.insert(activity)
                }
            }
        )
        QuickLogButton(
            label = "Meds",
            icon = Icons.Outlined.MedicalServices,
            color = Color.Red,
            modifier = Modifier.weight(1f),
            onClick = {
                selectedPetId?.let { petId ->
                    val activity = Activity(
                        petId = petId,
                        type = ActivityType.MEDICATION,
                        title = "Medication",
                        dateTime = LocalDateTime.now()
                    )
                    viewModel.insert(activity)
                }
            }
        )
    }
}

@Composable
private fun QuickLogButton(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}