package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditLaporanScreen(
    onBackClick: () -> Unit = {}
) {
    val pinkButton = Color(0xFFEFA7A9)
    val grayText = Color(0xFF888888)
    val grayField = Color(0xFFF5F5F5)
    val pinkLight = Color(0xFFFDE8E8)

    var namaBarang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf("Hilang") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tambah Laporan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Kategori Laporan
            Text(text = "Kategori Laporan", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedKategori == "Hilang") pinkLight else Color.White)
                        .clickable { selectedKategori = "Hilang" }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hilang",
                        color = if (selectedKategori == "Hilang") pinkButton else grayText,
                        fontWeight = if (selectedKategori == "Hilang") FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedKategori == "Ditemukan") pinkLight else Color.White)
                        .clickable { selectedKategori = "Ditemukan" }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ditemukan",
                        color = if (selectedKategori == "Ditemukan") pinkButton else grayText,
                        fontWeight = if (selectedKategori == "Ditemukan") FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Foto Barang
            Text(text = "Foto Barang", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(grayField)
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        tint = grayText,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ketuk untuk unggah foto", color = grayText, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nama Barang
            Text(text = "Nama Barang", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = namaBarang,
                onValueChange = { namaBarang = it },
                placeholder = { Text("Contoh: Dompet Kulit Cokelat", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tanggal Kejadian
            Text(text = "Tanggal Kejadian", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tanggal,
                onValueChange = { tanggal = it },
                placeholder = { Text("mm/dd/yyyy", color = Color.LightGray) },
                trailingIcon = {
                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = grayText)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // No Telepon
            Text(text = "No Telepon", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = noTelepon,
                onValueChange = { noTelepon = it },
                placeholder = { Text("089522658965", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Kategori
            Text(text = "Kategori", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = kategori,
                onValueChange = { kategori = it },
                placeholder = { Text("Aksesoris", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Deskripsi Detail
            Text(text = "Deskripsi Detail", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = { Text("Sebutkan ciri-ciri khusus, isi barang, atau merk...", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lokasi Terakhir
            Text(text = "Lokasi Terakhir", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lokasi,
                onValueChange = { lokasi = it },
                placeholder = { Text("Contoh: Stasiun Gambir atau Cafe ABC", color = Color.LightGray) },
                trailingIcon = {
                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = grayText)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField,
                    focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF3F3))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = pinkButton,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Laporan Anda akan dipublikasikan ke komunitas TemuBarang agar orang lain dapat membantu mencarinya. Pastikan informasi sudah benar.",
                        color = grayText,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkButton)
            ) {
                Text(
                    text = "Simpan Laporan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}