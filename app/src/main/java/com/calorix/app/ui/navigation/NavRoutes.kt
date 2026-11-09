package com.calorix.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddFood : Screen("add_food")
    data object History : Screen("history")
    data object Settings : Screen("settings")
}
