package com.example.findup.navigasi

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*
import com.example.findup.screen.HomeScreen
import com.example.findup.screen.LaporanScreen
import com.example.findup.screen.ProfileScreen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppNav() {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("home",    "Beranda",   Icons.Default.Home),
        BottomNavItem("laporan", "Laporanku", Icons.Default.Menu),
        BottomNavItem("profil",  "Profil",    Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector        = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(item.title)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color(0xFFF08080),
                            selectedTextColor   = Color(0xFFF08080),
                            indicatorColor      = Color(0xFFFFF0F0),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen()
            }
            composable("laporan") {
                LaporanScreen()
            }
            composable("profil") {
                ProfileScreen()
            }
        }
    }
}