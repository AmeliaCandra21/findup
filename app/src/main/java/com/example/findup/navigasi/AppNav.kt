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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findup.viewmodel.LaporanViewModel
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

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var totalUnread by remember { mutableStateOf(0) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("chats")
                .whereArrayContains("participants", currentUserId)
                .addSnapshotListener { snapshots, _ ->
                    var count = 0
                    snapshots?.documents?.forEach { doc ->
                        doc.reference.collection("messages")
                            .whereEqualTo("receiverId", currentUserId)
                            .whereEqualTo("isRead", false)
                            .get()
                            .addOnSuccessListener { msgs ->
                                count += msgs.size()
                                totalUnread = count
                            }
                    }
                    if (snapshots?.isEmpty == true) totalUnread = 0
                }
        }
    }



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
                            if (item.route == "Inbox" && totalUnread > 0) {
                                BadgedBox(badge = {
                                    Badge { Text(if (totalUnread > 99) "99+" else totalUnread.toString()) }
                                }) {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
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
                LaporanScreen(navController = navController)
            }
            composable("profil") {
                ProfileScreen(navController = rootNavController)
            }
            composable("TambahLaporan") {
                TambahLaporanScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ── Inbox ──────────────────────────────────────────────
            composable("Inbox") {
                InboxScreen(navController = navController)
            }

            // ── Chat dari Inbox (tanpa barang) ─────────────────────
            composable(
                route = "ChatDariInbox/{contactId}/{contactName}",
                arguments = listOf(
                    navArgument("contactId")   { type = NavType.StringType },
                    navArgument("contactName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contactId   = backStackEntry.arguments?.getString("contactId")   ?: ""
                val contactName = backStackEntry.arguments?.getString("contactName") ?: "User"

                val viewModel: LaporanViewModel = viewModel()
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val chatId = remember { listOf(currentUserId, contactId).sorted().joinToString("_") }

                LaunchedEffect(chatId) {
                    viewModel.tandaiSudahDibaca(chatId, currentUserId)
                }

                ChatScreen(
                    contactUserId = contactId,
                    contactName   = contactName,
                    onBack        = { navController.popBackStack() }
                )
            }

            composable(
                route = "EditLaporan/{laporanId}",
                arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
            ) { backStackEntry ->
                val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
                EditLaporanScreen(
                    laporanId   = laporanId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "DetailBarang/{laporanId}",
                arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
            ) { backStackEntry ->
                val laporanId = backStackEntry.arguments?.getString("laporanId") ?: ""
                DetailBarangScreen(
                    laporanId   = laporanId,
                    onBackClick = { navController.popBackStack() },
                    onChatClick = { userId, username, namaBarang, _ ->
                        val encodedName = java.net.URLEncoder.encode(namaBarang, "UTF-8")
                        val encodedUser = java.net.URLEncoder.encode(username, "UTF-8")
                        navController.navigate("Chat/$userId/$encodedUser/$encodedName")
                    }
                )
            }
            composable(
                route = "Chat/{userId}/{username}/{namaBarang}",
                arguments = listOf(
                    navArgument("userId")     { type = NavType.StringType },
                    navArgument("username")   { type = NavType.StringType },
                    navArgument("namaBarang") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId     = backStackEntry.arguments?.getString("userId")     ?: ""
                val username   = backStackEntry.arguments?.getString("username")   ?: ""
                val namaBarang = backStackEntry.arguments?.getString("namaBarang") ?: ""
                ChatScreen(
                    contactUserId = userId,
                    contactName   = username,
                    barangTemuan  = com.example.findup.screen.ItemBaanTemuan(
                        title    = "Tentang: $namaBarang",
                        desc     = "Hubungi pelapor untuk info lebih lanjut"
                    ),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}