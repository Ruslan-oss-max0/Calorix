package com.calorix.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calorix.app.data.FoodEntry
import com.calorix.app.ui.components.CalorieRing
import com.calorix.app.ui.components.EmptyStateView
import com.calorix.app.ui.components.FoodEntryItem
import com.calorix.app.ui.theme.AmberAccent
import com.calorix.app.ui.theme.BlueMeal
import com.calorix.app.ui.theme.EmeraldPrimary
import com.calorix.app.ui.theme.VioletMeal
import com.calorix.app.ui.viewmodel.CalorieUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: CalorieUiState,
    onNavigateToAddFood: () -> Unit,
    onDeleteFood: (FoodEntry) -> Unit,
    onClearFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onClearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Calorix",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = todayFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddFood,
                containerColor = EmeraldPrimary,
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Food")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calorie Ring & Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CalorieRing(
                            consumed = uiState.todayTotalCalories,
                            goal = uiState.dailyGoal,
                            progress = uiState.progressFraction,
                            isOverGoal = uiState.isOverGoal
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Quick summary row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryStat(label = "Goal", value = "${uiState.dailyGoal} kcal")
                            SummaryStat(label = "Consumed", value = "${uiState.todayTotalCalories} kcal")
                            SummaryStat(
                                label = if (uiState.isOverGoal) "Excess" else "Remaining",
                                value = if (uiState.isOverGoal) "+${uiState.excessCalories} kcal" else "${uiState.remainingCalories} kcal",
                                isHighlight = true
                            )
                        }
                    }
                }
            }

            // Meal Breakdown Cards
            item {
                Column {
                    Text(
                        text = "Meal Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealTypeCard(
                            title = "Breakfast",
                            calories = uiState.mealBreakdown.breakfast,
                            color = AmberAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MealTypeCard(
                            title = "Lunch",
                            calories = uiState.mealBreakdown.lunch,
                            color = EmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealTypeCard(
                            title = "Dinner",
                            calories = uiState.mealBreakdown.dinner,
                            color = BlueMeal,
                            modifier = Modifier.weight(1f)
                        )
                        MealTypeCard(
                            title = "Snacks",
                            calories = uiState.mealBreakdown.snack,
                            color = VioletMeal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Today's Meals Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${uiState.todayEntries.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.todayEntries.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Meals Logged Today",
                        subtitle = "Tap the + button below to log your breakfast, lunch, dinner, or snack.",
                        actionLabel = "Add Food",
                        onActionClick = onNavigateToAddFood
                    )
                }
            } else {
                items(
                    items = uiState.todayEntries,
                    key = { it.id }
                ) { entry ->
                    FoodEntryItem(
                        entry = entry,
                        onDelete = onDeleteFood
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isHighlight) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MealTypeCard(
    title: String,
    calories: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$calories kcal",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
