package com.example.glog.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val hasBottomBar: Boolean = true
) {
    // Pantallas principales (con bottom bar)
    object Home : Destination("home")
    object Cluster : Destination("cluster")
    object Profile : Destination("profile")

    // Pantallas secundarias (sin bottom bar)
    object GameDetails : Destination("gameInfo/{id}", hasBottomBar = true) {
        fun createRoute(id: String) = "gameInfo/$id"
    }

    object Settings : Destination("settings")
}

// Clase para representar items de bottom navigation
data class BottomNavItem(
    val destination: Destination,
    @StringRes val labelResId: Int,
    val icon: ImageVector,
    @StringRes val contentDescriptionResId: Int
)

// Lista de items para el bottom navigation
val bottomNavItems = listOf(
    BottomNavItem(
        destination = Destination.Home,
        labelResId = com.example.glog.R.string.nav_home,
        icon = Icons.Default.Home,
        contentDescriptionResId = com.example.glog.R.string.nav_home
    ),
    BottomNavItem(
        destination = Destination.Cluster,
        labelResId = com.example.glog.R.string.nav_cluster,
        icon = Icons.Default.DateRange,
        contentDescriptionResId = com.example.glog.R.string.nav_cluster
    ),
    BottomNavItem(
        destination = Destination.Profile,
        labelResId = com.example.glog.R.string.nav_profile,
        icon = Icons.Default.Person,
        contentDescriptionResId = com.example.glog.R.string.nav_profile
    )
)