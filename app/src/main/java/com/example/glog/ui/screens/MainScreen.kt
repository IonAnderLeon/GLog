package com.example.glog.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.glog.ui.navigation.AppBottomNavigationBar
import com.example.glog.ui.navigation.AppNavHost
import com.example.glog.ui.navigation.Destination
import com.example.glog.ui.navigation.bottomNavItems
import com.example.glog.ui.navigation.currentRoute

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)
    
    val showBottomBar = remember(currentRoute) {
        when (currentRoute) {
            Destination.Home.route,
            Destination.Cluster.route,
            Destination.Profile.route -> true
            else -> false
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}