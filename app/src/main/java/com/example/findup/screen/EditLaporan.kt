package com.example.findup.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.findup.viewmodel.LaporanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLaporanScreen(
    laporanId: String = "",
    onBackClick: () -> Unit = {},
    viewModel: LaporanViewModel = viewModel()
) {
    val context = LocalContext.current
    val pinkButton = Color(0xFFEFA7A9)
    val grayText = Color(0xFF888888)
    val grayField = Color(0xFFF5F5F5)

    var namaBarang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var existingFotoUrl by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("HILANG") }

    val datePickerState = rememberDatePickerState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> fotoUri = uri }

    LaunchedEffect(laporanId) {
        if (laporanId.isNotEmpty()) {
            viewModel.getLaporanById(laporanId) { laporan ->
                laporan?.let {
                    namaBarang = it.namaBarang
                    tanggal = it.tanggal
                    noTelepon = it.noTelepon
                    kategori = it.kategori
                    deskripsi = it.deskripsi
                    lokasi = it.lokasi
                    existingFotoUrl = it.fotoUrl
                    selectedStatus = it.status
                }
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

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
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Laporan", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = pinkButton)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ── Kategori Laporan ──────────────────────────────────────
            Text(text = "Kategori Laporan", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selectedStatus == "HILANG") Color(0xFFFFE4E4) else Color.Transparent)
                        .clickable { selectedStatus = "HILANG" }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Hilang",
                        fontSize = 14.sp,
                        fontWeight = if (selectedStatus == "HILANG") FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedStatus == "HILANG") Color(0xFFE8737A) else Color.Gray
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selectedStatus == "DITEMUKAN") Color(0xFFFFE4E4) else Color.Transparent)
                        .clickable { selectedStatus = "DITEMUKAN" }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Ditemukan",
                        fontSize = 14.sp,
                        fontWeight = if (selectedStatus == "DITEMUKAN") FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedStatus == "DITEMUKAN") Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Foto Barang ───────────────────────────────────────────
            Text(text = "Foto Barang", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(grayField)
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                    .clickable { if (!isUploading) galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    fotoUri != null -> {
                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Foto Baru",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f)).padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("Ketuk untuk ganti foto", color = Color.White, fontSize = 12.sp) }
                    }
                    existingFotoUrl.isNotEmpty() -> {
                        AsyncImage(
                            model = existingFotoUrl,
                            contentDescription = "Foto Barang",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f)).padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("Ketuk untuk ganti foto", color = Color.White, fontSize = 12.sp) }
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.CloudUpload, null, tint = grayText, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ketuk untuk unggah foto", color = grayText, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Nama Barang", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = namaBarang, onValueChange = { namaBarang = it },
                placeholder = { Text("Contoh: Dompet Kulit Cokelat", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Tanggal Kejadian", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tanggal, onValueChange = {}, readOnly = true,
                placeholder = { Text("mm/dd/yyyy", color = Color.LightGray) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = grayText)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "No Telepon", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = noTelepon, onValueChange = { noTelepon = it },
                placeholder = { Text("089522658965", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Kategori", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = kategori, onValueChange = { kategori = it },
                placeholder = { Text("Aksesoris", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Deskripsi Detail", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deskripsi, onValueChange = { deskripsi = it },
                placeholder = { Text("Sebutkan ciri-ciri khusus...", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp), maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Lokasi Terakhir", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lokasi, onValueChange = { lokasi = it },
                placeholder = { Text("Contoh: Stasiun Gambir", color = Color.LightGray) },
                trailingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = grayText) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE), focusedBorderColor = pinkButton,
                    unfocusedContainerColor = grayField, focusedContainerColor = grayField
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF3F3)).padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, null, tint = pinkButton, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Pastikan informasi yang kamu ubah sudah benar sebelum disimpan.",
                        color = grayText, fontSize = 12.sp, lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isUploading = true
                    if (fotoUri != null) {
                        viewModel.uploadFoto(
                            uri = fotoUri!!,
                            onSuccess = { url ->
                                viewModel.updateLaporan(
                                    id = laporanId,
                                    namaBarang = namaBarang,
                                    tanggal = tanggal,
                                    noTelepon = noTelepon,
                                    kategori = kategori,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    fotoUrl = url
                                )
                                isUploading = false
                                onBackClick()
                            },
                            onFailure = {
                                viewModel.updateLaporan(
                                    id = laporanId,
                                    namaBarang = namaBarang,
                                    tanggal = tanggal,
                                    noTelepon = noTelepon,
                                    kategori = kategori,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    fotoUrl = existingFotoUrl
                                )
                                isUploading = false
                                onBackClick()
                            }
                        )
                    } else {
                        viewModel.updateLaporan(
                            id = laporanId,
                            namaBarang = namaBarang,
                            tanggal = tanggal,
                            noTelepon = noTelepon,
                            kategori = kategori,
                            deskripsi = deskripsi,
                            lokasi = lokasi,
                            fotoUrl = existingFotoUrl
                        )
                        isUploading = false
                        onBackClick()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkButton)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Mengunggah foto...", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                } else {
                    Text("Ubah Laporan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}