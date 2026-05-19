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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.findup.data.Laporan
import com.example.findup.ui.theme.*
import com.example.findup.viewmodel.LaporanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    navController: NavController,
    viewModel: LaporanViewModel = viewModel()
) {
    val laporanList by viewModel.getLaporanByUser()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var itemToDelete by remember { mutableStateOf<Laporan?>(null) }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Laporan") },
            text = { Text("Yakin ingin menghapus laporan \"${itemToDelete!!.namaBarang}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.hapusLaporan(itemToDelete!!)
                    itemToDelete = null
                }) { Text("Hapus", color = RedDelete) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(PinkLight),
                            contentAlignment = Alignment.Center
                        ) { Text("📍", fontSize = 16.sp) }
                        Spacer(Modifier.width(8.dp))
                        Text("FindUp", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("TambahLaporan") },
                shape = CircleShape,
                containerColor = PinkPrimary,
                contentColor = White
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
        }
    ) { innerPadding ->

        if (laporanList.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Belum ada laporan", fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Laporan yang kamu buat\nakan muncul di sini",
                        fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate("TambahLaporan") },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Buat Laporan Pertama", color = White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Laporanku", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text("Kelola laporan barang yang telah kamu buat",
                        fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                }

                items(laporanList, key = { it.id }) { laporan ->
                    LaporanCardItem(
                        laporan = laporan,
                        onEdit = { navController.navigate("EditLaporan/${laporan.id}") },
                        onHapus = { itemToDelete = laporan }
                    )
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
fun LaporanCardItem(laporan: Laporan, onEdit: () -> Unit, onHapus: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                if (laporan.fotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = laporan.fotoUrl,
                        contentDescription = laporan.namaBarang,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) { Text("📦", fontSize = 48.sp) }
                }

                val badgeColor = if (laporan.status == "HILANG") Color(0xFFE53935) else Color(0xFF4CAF50)
                val badgeBg = if (laporan.status == "HILANG") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                val badgeText = if (laporan.status == "HILANG") Color(0xFFE53935) else Color(0xFF4CAF50)
                Box(
                    modifier = Modifier.padding(10.dp).align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp)).background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(laporan.status, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = badgeText)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(laporan.namaBarang, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary, modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null,
                            tint = TextSecondary, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(laporan.tanggal, fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Spacer(Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(laporan.lokasi, fontSize = 13.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit",
                            tint = TextSecondary, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit", fontSize = 13.sp, color = TextSecondary)
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onHapus, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus",
                            tint = RedDelete, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Hapus", fontSize = 13.sp, color = RedDelete)
                    }
                }
            }
        }
    }
}