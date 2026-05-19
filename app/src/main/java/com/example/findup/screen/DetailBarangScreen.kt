package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.findup.data.Laporan
import com.example.findup.viewmodel.LaporanViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DetailBarangScreen(
    laporanId: String,
    onBackClick: () -> Unit = {},
    onChatClick: (userId: String, username: String, namaBarang: String, fotoUrl: String) -> Unit = { _, _, _, _ -> },
    viewModel: LaporanViewModel = viewModel()
) {
    val pinkButton    = Color(0xFFEFA7A9)
    val grayText      = Color(0xFF888888)
    val grayField     = Color(0xFFF2F2F2)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var laporan by remember { mutableStateOf<Laporan?>(null) }

    LaunchedEffect(laporanId) {
        viewModel.getLaporanById(laporanId) { laporan = it }
    }

    if (laporan == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = pinkButton)
        }
        return
    }

    val data = laporan!!
    val isOwner = data.userId == currentUserId
    val statusColor = if (data.status == "HILANG") Color(0xFFE53935) else Color(0xFF4CAF50)
    val statusBg    = if (data.status == "HILANG") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // Foto barang
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                if (data.fotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = data.fotoUrl,
                        contentDescription = data.namaBarang,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) { Text("📦", fontSize = 64.sp) }
                }

                // Tombol back
                Box(
                    modifier = Modifier.statusBarsPadding().padding(16.dp)
                        .size(36.dp).clip(CircleShape).background(Color.White)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali",
                        tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }

            // Konten bawah
            Box(
                modifier = Modifier.fillMaxWidth().offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Badge status
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(data.status, color = statusColor,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(data.namaBarang, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, color = Color.Black)

                    Spacer(Modifier.height(16.dp))

                    // Tanggal & Kategori
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoBox(modifier = Modifier.weight(1f),
                            icon = { Icon(Icons.Outlined.CalendarMonth, null, tint = pinkButton, modifier = Modifier.size(22.dp)) },
                            label = "TANGGAL", value = data.tanggal)
                        InfoBox(modifier = Modifier.weight(1f),
                            icon = { Icon(Icons.Outlined.Category, null, tint = pinkButton, modifier = Modifier.size(22.dp)) },
                            label = "KATEGORI", value = data.kategori)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Deskripsi Lengkap", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(data.deskripsi, color = grayText, fontSize = 14.sp, lineHeight = 22.sp)

                    Spacer(Modifier.height(24.dp))

                    Text("Lokasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(grayField).padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = pinkButton, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(data.lokasi, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Dilaporkan oleh
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(grayField).padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape)
                                    .background(Color(0xFFFFCDD2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(data.username.firstOrNull()?.toString() ?: "?",
                                    color = Color(0xFFE8737A), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dilaporkan oleh", fontSize = 10.sp, color = grayText)
                                Text(data.username, fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = Color.Black)
                                Spacer(Modifier.height(4.dp))
                                Text("No Telepon", fontSize = 10.sp, color = grayText)
                                Text(data.noTelepon, fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = Color.Black)
                            }
                        }
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        // Tombol bawah — berbeda tergantung pemilik
        Box(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(Color.White).padding(16.dp)
        ) {
            if (isOwner) {
                // Postingan sendiri → Edit
                Button(
                    onClick = { /* navigasi ke edit sudah dari HomeScreen */ },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = pinkButton)
                ) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Laporan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            } else {
                // Postingan orang lain → DM
                Button(
                    onClick = {
                        onChatClick(data.userId, data.username, data.namaBarang, data.fotoUrl)
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = pinkButton)
                ) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kirim Pesan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun InfoBox(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEF0FB)).padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) { icon() }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color(0xFF888888), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            }
        }
    }
}