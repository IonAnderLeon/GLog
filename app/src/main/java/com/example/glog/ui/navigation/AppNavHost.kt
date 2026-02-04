package com.example.glog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.glog.ui.screens.ClusterScreen
import com.example.glog.ui.screens.HomeScreen
import com.example.glog.ui.screens.UserScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.HOME -> HomeScreen()
                    Destination.CLUSTER -> ClusterScreen()
                    Destination.USER -> UserScreen()
                }
            }
        }
    }
}