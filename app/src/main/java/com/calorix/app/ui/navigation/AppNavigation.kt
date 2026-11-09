package com.calorix.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calorix.app.ui.screens.AddFoodScreen
import com.calorix.app.ui.screens.HistoryScreen
import com.calorix.app.ui.screens.HomeScreen
import com.calorix.app.ui.screens.SettingsScreen
import com.calorix.app.ui.viewmodel.CalorieViewModel

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Today : BottomNavItem(Screen.Home.route, "Today", Icons.Filled.Home, Icons.Outlined.Home)
    data object History : BottomNavItem(Screen.History.route, "History", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    data object Settings : BottomNavItem(Screen.Settings.route, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun AppNavigation(
    viewModel: CalorieViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Today,
        BottomNavItem.History,
        BottomNavItem.Settings
    )

    // Only show bottom navigation on primary tabs
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) },
                    onDeleteFood = { viewModel.deleteFood(it) },
                    onClearFeedback = { viewModel.clearFeedbackMessage() }
                )
            }

            composable(Screen.AddFood.route) {
                AddFoodScreen(
                    onSaveFood = { name, calories, mealType ->
                        viewModel.addFood(name, calories, mealType)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    uiState = uiState,
                    onDeleteFood = { viewModel.deleteFood(it) },
                    onClearFeedback = { viewModel.clearFeedbackMessage() },
                    onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    onSaveGoal = { viewModel.setDailyGoal(it) },
                    onClearAllData = { viewModel.clearAllData() },
                    onClearFeedback = { viewModel.clearFeedbackMessage() }
                )
            }
        }
    }
}
