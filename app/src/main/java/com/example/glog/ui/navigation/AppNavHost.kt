package com.example.glog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.glog.ui.screens.ClusterScreen
import com.example.glog.ui.screens.GameInfoScreen
import com.example.glog.ui.screens.HomeScreen
import com.example.glog.ui.screens.UserScreen

// ui/navigation/AppNavHost.kt
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        // Pantalla Home
        composable(Destination.Home.route) {
            HomeScreen(
                navController = navController
            )
        }

        // Pantalla Cluster
        composable(Destination.Cluster.route) {
            ClusterScreen(navController = navController)
        }

        // Pantalla Profile
        composable(Destination.Profile.route) {
            UserScreen(navController = navController)
        }

        // Pantalla de detalles (secundaria)
        composable(
            route = Destination.GameDetails.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            GameInfoScreen(
                navController =navController
            )
        }

    }
}