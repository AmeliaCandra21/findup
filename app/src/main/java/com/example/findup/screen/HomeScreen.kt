package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.findup.data.Laporan
import com.example.findup.viewmodel.LaporanViewModel
import com.google.firebase.auth.FirebaseAuth

val FindUpPink    = Color(0xFFE8737A)
val PinkLight     = Color(0xFFFDE8E9)
val PinkBorder    = Color(0xFFF4BBBE)
val TextPrimary   = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF888888)
val BgCard        = Color(0xFFFFFFFF)
val BgScreen      = Color(0xFFF7F7F7)
val GreenFound    = Color(0xFF4CAF50)
val RedLost       = Color(0xFFE53935)

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LaporanViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }   // ← state query search
    val filters = listOf("Semua", "Hilang", "Ditemukan")
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var semuaLaporan by remember { mutableStateOf<List<Laporan>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.fetchAllLaporanFromFirestore { semuaLaporan = it }
    }

    // ── Filter: status + search query ────────────────────────────────
    val filteredLaporan = semuaLaporan
        .filter { laporan ->
            when (selectedFilter) {
                1 -> laporan.status == "HILANG"
                2 -> laporan.status == "DITEMUKAN"
                else -> true
            }
        }
        .filter { laporan ->
            if (searchQuery.isBlank()) true
            else {
                val q = searchQuery.trim().lowercase()
                laporan.namaBarang.lowercase().contains(q) ||
                        laporan.lokasi.lowercase().contains(q) ||
                        laporan.kategori.lowercase().contains(q) ||
                        laporan.username.lowercase().contains(q) ||
                        laporan.deskripsi.lowercase().contains(q)
            }
        }

    Scaffold(
        containerColor = BgScreen,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("TambahLaporan") },
                containerColor = FindUpPink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Laporan")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            TopAppBarSection()
            Spacer(Modifier.height(16.dp))

            // ── Search bar sekarang terhubung ke state ────────────────
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(Modifier.height(16.dp))
            FilterRow(
                filters = filters,
                selectedIndex = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            Spacer(Modifier.height(16.dp))

            // ── Teks hasil pencarian kalau ada query ──────────────────
            if (searchQuery.isNotBlank()) {
                Text(
                    text = if (filteredLaporan.isEmpty())
                        "Tidak ada hasil untuk \"$searchQuery\""
                    else
                        "${filteredLaporan.size} hasil untuk \"$searchQuery\"",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (filteredLaporan.isEmpty()) {
                if (searchQuery.isNotBlank()) {
                    // Empty state khusus search
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Tidak ditemukan barang\ndengan kata kunci \"$searchQuery\"",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(onClick = { searchQuery = "" }) {
                                Text("Hapus pencarian", color = FindUpPink)
                            }
                        }
                    }
                } else {
                    EmptyFeedState(onTambahClick = { navController.navigate("TambahLaporan") })
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredLaporan) { laporan ->
                        LaporanCard(
                            laporan = laporan,
                            onClick = {
                                if (laporan.userId == currentUserId) {
                                    navController.navigate("EditLaporan/${laporan.id}")
                                } else {
                                    navController.navigate("DetailBarang/${laporan.id}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFeedState(onTambahClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(90.dp).clip(CircleShape).background(PinkLight),
                contentAlignment = Alignment.Center
            ) { Text("📦", fontSize = 36.sp) }
            Spacer(Modifier.height(20.dp))
            Text("Belum ada laporan", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Jadilah yang pertama melaporkan\nbarang hilang atau temuan!",
                fontSize = 14.sp, color = TextSecondary,
                textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onTambahClick,
                colors = ButtonDefaults.buttonColors(containerColor = FindUpPink),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Buat Laporan", color = Color.White)
            }
        }
    }
}

@Composable
fun LaporanCard(laporan: Laporan, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                if (laporan.fotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = laporan.fotoUrl,
                        contentDescription = laporan.namaBarang,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) { Text("📦", fontSize = 32.sp) }
                }

                val badgeColor = if (laporan.status == "HILANG") RedLost else GreenFound
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(RoundedCornerShape(50)).background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(laporan.status, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(18.dp).clip(CircleShape).background(PinkLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = laporan.username.firstOrNull()?.toString() ?: "?",
                            fontSize = 9.sp, color = FindUpPink, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = laporan.username,
                        fontSize = 10.sp, color = TextSecondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = laporan.namaBarang,
                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null,
                        tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(laporan.lokasi, fontSize = 11.sp, color = TextSecondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun TopAppBarSection() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(PinkLight),
            contentAlignment = Alignment.Center
        ) { Text("F", color = FindUpPink, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        Spacer(Modifier.width(8.dp))
        Text("FindUp", color = FindUpPink, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}

// ── SearchBar sekarang terima query dan onQueryChange dari luar ───────────────
@Composable
fun SearchBar(
    query: String = "",
    onQueryChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari nama, lokasi, kategori...", color = TextSecondary, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Text("✕", fontSize = 14.sp, color = TextSecondary)
                }
            }
        },
        shape = RoundedCornerShape(50),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FindUpPink,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    )
}

@Composable
fun FilterRow(filters: List<String>, selectedIndex: Int, onFilterSelected: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        filters.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clip(RoundedCornerShape(50))
                    .background(if (isSelected) FindUpPink else Color.Transparent)
                    .border(1.dp, if (isSelected) FindUpPink else PinkBorder, RoundedCornerShape(50))
                    .clickable { onFilterSelected(index) }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(label,
                    color = if (isSelected) Color.White else FindUpPink,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp)
            }
            Spacer(Modifier.width(8.dp))
        }
        Spacer(Modifier.weight(1f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                .background(PinkLight).clickable { }
        ) { Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = FindUpPink) }
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = selectedTab == 0, onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FindUpPink, selectedTextColor = FindUpPink,
                indicatorColor = PinkLight, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
        )
        NavigationBarItem(
            selected = selectedTab == 1, onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.List, contentDescription = "Laporanku") },
            label = { Text("Laporanku", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FindUpPink, selectedTextColor = FindUpPink,
                indicatorColor = PinkLight, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
        )
        NavigationBarItem(
            selected = selectedTab == 2, onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FindUpPink, selectedTextColor = FindUpPink,
                indicatorColor = PinkLight, unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
        )
    }
}