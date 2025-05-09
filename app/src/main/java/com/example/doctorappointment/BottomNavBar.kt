package com.example.doctorappointment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf("home", "appointments", "doctors", "chatbot")
    val icons = listOf(Icons.Default.Home, Icons.Default.Event, Icons.Default.LocalHospital, Icons.Default.Chat)
    val labels = listOf("Home", "Appointments", "Doctors", "Chatbot")
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) },
                selected = currentRoute == screen,
                onClick = {
                    if (currentRoute != screen) {
                        navController.navigate(screen)
                    }
                }
            )
        }
    }
}
