package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findup.ui.theme.*

// ─── Model ────────────────────────────────────────────────────────────────────
enum class StatusLaporan { AKTIF, SELESAI }

data class LaporanItem(
    val id       : String,
    val nama     : String,
    val lokasi   : String,
    val waktu    : String,
    val status   : StatusLaporan,
    val imageUrl : String? = null
)

// ─── Halaman Laporanku ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    onTambah    : () -> Unit = {},
    onEdit      : (LaporanItem) -> Unit = {},
    onHapus     : (LaporanItem) -> Unit = {},
    onProfile   : () -> Unit = {},
    onBeranda   : () -> Unit = {}
) {
    val laporanList = remember {
        mutableStateListOf(
            LaporanItem(
                id       = "1",
                nama     = "MacBook Air M1 Silver",
                lokasi   = "Perpustakaan Pusat, Lt. 2",
                waktu    = "2 Jam lalu",
                status   = StatusLaporan.AKTIF,
                imageUrl = "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=600"
            ),
            LaporanItem(
                id       = "2",
                nama     = "Jam Tangan Fossil",
                lokasi   = "Kantin Teknik",
                waktu    = "3 Hari lalu",
                status   = StatusLaporan.SELESAI,
                imageUrl = "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600"
            ),
            LaporanItem(
                id       = "3",
                nama     = "Kunci Rumah & Gantungan",
                lokasi   = "Masjid Al-Ikhlas",
                waktu    = "5 Jam lalu",
                status   = StatusLaporan.AKTIF,
                imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600"
            )
        )
    }

    val jumlahAktif   = laporanList.count { it.status == StatusLaporan.AKTIF }
    val jumlahSelesai = laporanList.count { it.status == StatusLaporan.SELESAI }

    // Dialog konfirmasi hapus
    var itemToDelete by remember { mutableStateOf<LaporanItem?>(null) }
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title   = { Text("Hapus Laporan") },
            text    = { Text("Yakin ingin menghapus laporan \"${itemToDelete!!.nama}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    laporanList.remove(itemToDelete)
                    onHapus(itemToDelete!!)
                    itemToDelete = null
                }) {
                    Text("Hapus", color = RedDelete)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = { LaporanTopBar(onProfile = onProfile) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onTambah,
                shape          = CircleShape,
                containerColor = PinkPrimary,
                contentColor   = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Laporan")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header judul
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text       = "Laporanku",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Kelola daftar barang yang telah Anda laporkan di platform.",
                    fontSize = 13.sp,
                    color    = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Kartu ringkasan statistik
            item {
                StatistikRow(jumlahAktif = jumlahAktif, jumlahSelesai = jumlahSelesai)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Daftar laporan
            items(laporanList, key = { it.id }) { item ->
                LaporanCard(
                    item    = item,
                    onEdit  = { onEdit(item) },
                    onHapus = { itemToDelete = item }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaporanTopBar(onProfile: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo FindUp
                Box(
                    modifier         = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "TemuBarang",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PinkPrimary
                )
            }
        },
        actions = {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD7C4B0)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
    HorizontalDivider(color = Color(0xFFF0F0F0))
}

// ─── Statistik ─────────────────────────────────────────────────────────────────
@Composable
private fun StatistikRow(jumlahAktif: Int, jumlahSelesai: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label    = "Aktif",
            value    = jumlahAktif.toString().padStart(2, '0'),
            valueColor = PinkPrimary,
            icon     = "📋"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label    = "Selesai",
            value    = jumlahSelesai.toString().padStart(2, '0'),
            valueColor = GreenActive,
            icon     = "✅"
        )
    }
}

@Composable
private fun StatCard(
    modifier   : Modifier,
    label      : String,
    value      : String,
    valueColor : Color,
    icon       : String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = label, fontSize = 13.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text       = value,
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                color      = valueColor
            )
        }
    }
}

// ─── Kartu Laporan ────────────────────────────────────────────────────────────
@Composable
private fun LaporanCard(
    item    : LaporanItem,
    onEdit  : () -> Unit,
    onHapus : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Gambar + badge status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Gambar barang
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model              = item.imageUrl,
                        contentDescription = item.nama,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier         = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📦", fontSize = 48.sp)
                    }
                }

                // Badge status di kiri atas
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (item.status == StatusLaporan.AKTIF) BadgeAktif
                            else BadgeSelesai
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text       = if (item.status == StatusLaporan.AKTIF) "Aktif" else "Selesai",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (item.status == StatusLaporan.AKTIF) GreenActive else TextSecondary
                    )
                }
            }

            // Info barang
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                // Nama + waktu
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = item.nama,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        modifier   = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Default.DateRange,
                            contentDescription = null,
                            tint               = TextSecondary,
                            modifier           = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text     = item.waktu,
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Lokasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint               = TextSecondary,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text     = item.lokasi,
                        fontSize = 13.sp,
                        color    = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(8.dp))

                // Tombol aksi
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Edit
                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint               = TextSecondary,
                            modifier           = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edit", fontSize = 13.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Hapus
                    TextButton(
                        onClick = onHapus,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint               = RedDelete,
                            modifier           = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Hapus", fontSize = 13.sp, color = RedDelete)
                    }
                }
            }
        }
    }
}

// ─── Bottom Navigation ─────────────────────────────────────────────────────────
@Composable
private fun LaporanBottomNav(
    onBeranda : () -> Unit,
    onProfile : () -> Unit
) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 4.dp
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor   = PinkPrimary,
            selectedTextColor   = PinkPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary,
            indicatorColor      = PinkLight
        )

        NavigationBarItem(
            selected = false,
            onClick  = onBeranda,
            icon     = { Text("🏠", fontSize = 20.sp) },
            label    = { Text("Beranda", fontSize = 11.sp) },
            colors   = navColors
        )
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = { Text("📋", fontSize = 20.sp) },
            label    = { Text("Laporanku", fontSize = 11.sp) },
            colors   = navColors
        )
        NavigationBarItem(
            selected = false,
            onClick  = onProfile,
            icon     = { Text("👤", fontSize = 20.sp) },
            label    = { Text("Profil", fontSize = 11.sp) },
            colors   = navColors
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LaporanScreenPreview() {
    MaterialTheme {
        LaporanScreen()
    }
}