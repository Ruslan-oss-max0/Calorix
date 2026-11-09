package com.calorix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calorix.app.data.FoodEntry
import com.calorix.app.ui.components.EmptyStateView
import com.calorix.app.ui.components.FoodEntryItem
import com.calorix.app.ui.theme.EmeraldPrimary
import com.calorix.app.ui.viewmodel.CalorieUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: CalorieUiState,
    onDeleteFood: (FoodEntry) -> Unit,
    onClearFeedback: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Food History & Logs",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.allEntries.isEmpty()) {
            EmptyStateView(
                title = "No History Yet",
                subtitle = "Your logged meals will be grouped here day by day so you can track your consistency.",
                actionLabel = "Log Today's Meal",
                onActionClick = onNavigateToAddFood,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total overview pill
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "All Recorded Days (${uiState.groupedHistoryEntries.keys.size})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.allEntries.size} total items",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = EmeraldPrimary
                        )
                    }
                }

                // Group entries by date
                uiState.groupedHistoryEntries.forEach { (dateKey, entries) ->
                    val dayTotal = entries.sumOf { it.calories }
                    val displayDate = FoodEntry.getDisplayDate(dateKey)

                    item(key = "header_$dateKey") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$dayTotal kcal",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = EmeraldPrimary
                            )
                        }
                    }

                    items(
                        items = entries,
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
}
