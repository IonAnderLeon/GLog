package com.example.glog.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String
) {
    // Pantallas principales (con bottom bar)
    object Home : Destination("home")
    object Cluster : Destination("cluster")
    object Profile : Destination("profile")

    // Pantallas secundarias (sin bottom bar)
    object GameDetails : Destination("gameInfo/{id}") {
        fun createRoute(id: String) = "gameInfo/$id"
    }

    object Settings : Destination("settings")
}

// Clase para representar items de bottom navigation
data class BottomNavItem(
    val destination: Destination,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
)

// Lista de items para el bottom navigation
val bottomNavItems = listOf(
    BottomNavItem(
        destination = Destination.Home,
        label = "Home",
        icon = Icons.Default.Home,
        contentDescription = "Home"
    ),
    BottomNavItem(
        destination = Destination.Cluster,
        label = "Cluster",
        icon = Icons.Default.DateRange,
        contentDescription = "Cluster"
    ),
    BottomNavItem(
        destination = Destination.Profile,
        label = "Profile",
        icon = Icons.Default.Person,
        contentDescription = "Profile"
    )
)