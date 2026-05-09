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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Warna tema FindUp ────────────────────────────────────────────────
val FindUpPink   = Color(0xFFE8737A)
val PinkLight    = Color(0xFFFDE8E9)
val PinkBorder   = Color(0xFFF4BBBE)
val TextPrimary  = Color(0xFF1A1A2E)
val TextSecondary= Color(0xFF888888)
val BgCard       = Color(0xFFFFFFFF)
val BgScreen     = Color(0xFFF7F7F7)
val GreenFound   = Color(0xFF4CAF50)
val RedLost      = Color(0xFFE53935)

// ── Model Data ───────────────────────────────────────────────────────
enum class PostStatus { HILANG, DITEMUKAN }

data class Post(
    val id: Int,
    val title: String,
    val location: String,
    val status: PostStatus,
    val imageUrl: String? = null
)

// ── Sample data ──────────────────────────────────────────────────────
val samplePosts = listOf(
    Post(1, "iPhone 13 Pro Max", "Bandung",          PostStatus.HILANG,
        "https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=400"),
    Post(2, "Kunci Honda",       "Area Parkir Mall", PostStatus.DITEMUKAN,
        "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"),
    Post(3, "Dompet Kulit",      "Stasiun Sudirman", PostStatus.HILANG,
        "https://images.unsplash.com/photo-1627123424574-724758594e93?w=400"),
    Post(4, "Kacamata Hitam",    "Lobi Kantor",      PostStatus.DITEMUKAN,
        null)
)

// ────────────────────────────────────────────────────────────────────
// MAIN SCREEN
// ────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf(0) }  // 0=Semua 1=Hilang 2=Temu

    val filters = listOf("Semua", "Hilang", "Temu")

    val filteredPosts = when (selectedFilter) {
        1 -> samplePosts.filter { it.status == PostStatus.HILANG }
        2 -> samplePosts.filter { it.status == PostStatus.DITEMUKAN }
        else -> samplePosts
    }

    Scaffold(
        containerColor = BgScreen,

        // ── FAB ─────────────────────────────────────────────────────
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* navigasi ke form lapor */ },
                containerColor = FindUpPink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Laporan")
            }
        },

        // ── Bottom Navigation ────────────────────────────────────────
        bottomBar = {
            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            // ── Logo / App Bar ───────────────────────────────────────
            TopAppBarSection()

            Spacer(Modifier.height(16.dp))

            // ── Search Bar ───────────────────────────────────────────
            SearchBar()

            Spacer(Modifier.height(16.dp))

            // ── Filter Chips ─────────────────────────────────────────
            FilterRow(
                filters = filters,
                selectedIndex = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(Modifier.height(16.dp))

            // ── Grid Postingan ───────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPosts) { post ->
                    PostCard(post = post)
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────
// TOP APP BAR
// ────────────────────────────────────────────────────────────────────
@Composable
fun TopAppBarSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo placeholder (ganti dengan Image(painterResource...) jika ada aset)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PinkLight),
            contentAlignment = Alignment.Center
        ) {
            Text("F", color = FindUpPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "FindUp",
            color = FindUpPink,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// SEARCH BAR
// ────────────────────────────────────────────────────────────────────
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = {
            Text(
                "Cari barang hilang atau ditemukan...",
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
        },
        shape = RoundedCornerShape(50),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = FindUpPink,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    )
}

// ────────────────────────────────────────────────────────────────────
// FILTER ROW
// ────────────────────────────────────────────────────────────────────
@Composable
fun FilterRow(
    filters: List<String>,
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        filters.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) FindUpPink else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) FindUpPink else PinkBorder,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onFilterSelected(index) }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else FindUpPink,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.width(8.dp))
        }

        Spacer(Modifier.weight(1f))

        // Filter/Sort icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PinkLight)
                .clickable { /* buka filter dialog */ }
        ) {
            Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = FindUpPink)
        }
    }
}

// ────────────────────────────────────────────────────────────────────
// POST CARD
// ────────────────────────────────────────────────────────────────────
@Composable
fun PostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            // ── Gambar + Badge ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                if (post.imageUrl != null) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = post.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    // Placeholder jika tidak ada gambar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = Color(0xFFBBBBBB),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Badge status (HILANG / DITEMUKAN)
                StatusBadge(
                    status = post.status,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            // ── Info ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = post.location,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────
// STATUS BADGE
// ────────────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: PostStatus, modifier: Modifier = Modifier) {
    val bgColor   = if (status == PostStatus.HILANG) RedLost else GreenFound
    val label     = if (status == PostStatus.HILANG) "HILANG" else "DITEMUKAN"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// BOTTOM NAVIGATION
// ────────────────────────────────────────────────────────────────────
@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick  = { onTabSelected(0) },
            icon     = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label    = { Text("Beranda", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = FindUpPink,
                selectedTextColor   = FindUpPink,
                indicatorColor      = PinkLight,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick  = { onTabSelected(1) },
            icon     = { Icon(Icons.Default.List, contentDescription = "Laporanku") },
            label    = { Text("Laporanku", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = FindUpPink,
                selectedTextColor   = FindUpPink,
                indicatorColor      = PinkLight,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick  = { onTabSelected(2) },
            icon     = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label    = { Text("Profil", fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = FindUpPink,
                selectedTextColor   = FindUpPink,
                indicatorColor      = PinkLight,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}

// ────────────────────────────────────────────────────────────────────
// PREVIEW
// ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}