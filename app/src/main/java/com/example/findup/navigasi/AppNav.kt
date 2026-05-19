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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.findup.screen.*

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppNav(rootNavController: NavController) {
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
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
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
                HomeScreen(navController = navController)
            }
            composable("laporan") {
                LaporanScreen(
                    navController = navController
                )
            }
            composable("profil") {
                ProfileScreen(navController = rootNavController)
            }
            composable("TambahLaporan") {
                TambahLaporanScreen(
                    onBackClick = { navController.popBackStack() } // ← cukup popBackStack saja
                )
            }
            composable(
                route = "EditLaporan/{laporanId}",
                arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
            ) { backStackEntry ->
                val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
                EditLaporanScreen(
                    laporanId = laporanId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "DetailBarang/{laporanId}",
                arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
            ) { backStackEntry ->
                val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
                DetailBarangScreen(
                    laporanId = laporanId,
                    onBackClick = { navController.popBackStack() },
                    onChatClick = { userId, username, namaBarang, fotoUrl ->
                        navController.navigate("Chat/$userId/$username/$namaBarang/$fotoUrl")
                    }
                )
            }
            composable(
                route = "Chat/{userId}/{username}/{namaBarang}/{fotoUrl}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("username") { type = NavType.StringType },
                    navArgument("namaBarang") { type = NavType.StringType },
                    navArgument("fotoUrl") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId     = backStackEntry.arguments?.getString("userId") ?: ""
                val username   = backStackEntry.arguments?.getString("username") ?: ""
                val namaBarang = backStackEntry.arguments?.getString("namaBarang") ?: ""
                val fotoUrl    = backStackEntry.arguments?.getString("fotoUrl") ?: ""
                ChatScreen(
                    contactName  = username,
                    contactPhoto = fotoUrl.ifEmpty { null },
                    barangTemuan = com.example.findup.screen.ItemBaanTemuan(
                        title    = "Tentang: $namaBarang",
                        desc     = "Hubungi pelapor untuk info lebih lanjut",
                        imageUrl = fotoUrl.ifEmpty { null }
                    ),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}