package com.example.glog.ui.navigation

@Composable
fun AppBottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    val currentRoute = currentRoute(navController)

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = {
                    // Navegar solo si no estamos ya en esa pantalla
                    if (currentRoute != item.destination.route) {
                        navController.navigate(item.destination.route) {
                            // Configuración para mantener estado
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

// Helper para obtener la ruta actual
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}