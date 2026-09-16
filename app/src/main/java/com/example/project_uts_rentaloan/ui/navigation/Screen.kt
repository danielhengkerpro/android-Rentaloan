package com.example.project_uts_rentaloan.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object AdminDashboard : Screen("admin_dashboard")
    object UserHome : Screen("user_home")
    object UserLoans : Screen("user_loans")
    object Settings : Screen("settings")
}
