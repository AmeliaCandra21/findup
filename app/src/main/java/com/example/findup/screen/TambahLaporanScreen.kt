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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahLaporanScreen() {

    var namaBarang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var kategoriBarang by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val pink = Color(0xFFF5A5A5)
    val softPink = Color(0xFFFFF1F1)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
                        tanggal = sdf.format(java.util.Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TambahLaporan",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = "Foto Barang",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(
                        1.dp,
                        Color.LightGray,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ketuk untuk unggah foto",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                label = "Nama Barang",
                value = namaBarang,
                onValueChange = { namaBarang = it },
                placeholder = "Contoh: Dompet Kulit Cokelat"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tanggal Kejadian",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                placeholder = { Text("mm/dd/yyyy") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF5A5A5),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                label = "No Telepon",
                value = noTelepon,
                onValueChange = { noTelepon = it },
                placeholder = "089522658965"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                label = "Kategori",
                value = kategoriBarang,
                onValueChange = { kategoriBarang = it },
                placeholder = "Aksesoris"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                label = "Deskripsi Detail",
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = "Sebutkan ciri-ciri barang..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                label = "Lokasi Terakhir",
                value = lokasi,
                onValueChange = { lokasi = it },
                placeholder = "Contoh: Stasiun Gambir",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF5F7FF))
                    .padding(14.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = pink
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Laporan Anda akan dipublikasikan ke komunitas TemuBarang agar orang lain dapat membantu mencarinya.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pink
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Simpan Laporan",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: @Composable (() -> Unit)? = null
) {

    Column {

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = placeholder)
            },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF5A5A5),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            singleLine = true
        )
    }
}