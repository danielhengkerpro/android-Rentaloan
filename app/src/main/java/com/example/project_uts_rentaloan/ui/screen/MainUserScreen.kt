package com.example.project_uts_rentaloan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_uts_rentaloan.ui.screen.user.HomeSection
import com.example.project_uts_rentaloan.ui.screen.user.PinjamSection
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel
import com.example.project_uts_rentaloan.ui.viewmodel.ItemViewModel

sealed class UserTab(val title: String, val icon: ImageVector) {
    object Pinjam : UserTab("Pinjam", Icons.Default.ShoppingCart)
    object Home : UserTab("Home", Icons.Default.Home)
    object Settings : UserTab("Settings", Icons.Default.Person)
}

@Composable
fun MainUserScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<UserTab>(UserTab.Home) }
    val tabs = listOf(UserTab.Pinjam, UserTab.Home, UserTab.Settings)

    Scaffold(
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(NeoBlack)
                )
                NavigationBar(
                    containerColor = NeoWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier.padding(bottom = 0.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            icon = { 
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(if (isSelected) NeoYellow else Color.Transparent)
                                        .border(if (isSelected) 2.dp else 0.dp, NeoBlack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        tab.icon, 
                                        contentDescription = tab.title,
                                        tint = NeoBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            label = { 
                                Text(
                                    tab.title, 
                                    color = NeoBlack,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                                ) 
                            },
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        val modifier = Modifier
            .fillMaxSize()
            .background(NeoYellow)
            .padding(padding)
        when (selectedTab) {
            UserTab.Pinjam -> {
                PinjamSection(
                    viewModel = itemViewModel,
                    authViewModel = authViewModel,
                    modifier = modifier
                )
            }
            UserTab.Home -> {
                HomeSection(
                    viewModel = itemViewModel,
                    authViewModel = authViewModel,
                    modifier = modifier
                )
            }
            UserTab.Settings -> {
                SettingsScreen(
                    authViewModel = authViewModel,
                    onBack = { selectedTab = UserTab.Home },
                    onLogout = onLogout,
                    modifier = modifier
                )
            }
        }
    }
}
