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
fun TambahLaporanScreen(
    onBackClick: () -> Unit = {},
    viewModel: LaporanViewModel = viewModel()
) {
    val context = LocalContext.current

    var namaBarang by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var kategoriBarang by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("HILANG") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> fotoUri = uri }

    val pink = Color(0xFFE8737A)
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
                title = { Text("Tambah Laporan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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

            // ── 1. PILIHAN STATUS ─────────────────────────────────────
            Text(text = "Jenis Laporan", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                val hilangSelected = selectedStatus == "HILANG"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hilangSelected) Color(0xFFE53935) else Color(0xFFF5F5F5))
                        .border(1.dp, if (hilangSelected) Color(0xFFE53935) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .clickable { selectedStatus = "HILANG" }
                        .padding(vertical = 14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Saya Kehilangan",
                            fontSize = 13.sp,
                            fontWeight = if (hilangSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (hilangSelected) Color.White else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                val ditemukanSelected = selectedStatus == "DITEMUKAN"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (ditemukanSelected) Color(0xFF4CAF50) else Color(0xFFF5F5F5))
                        .border(1.dp, if (ditemukanSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .clickable { selectedStatus = "DITEMUKAN" }
                        .padding(vertical = 14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Saya Menemukan",
                            fontSize = 13.sp,
                            fontWeight = if (ditemukanSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (ditemukanSelected) Color.White else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── 2. FOTO BARANG ────────────────────────────────────────
            Text(text = "Foto Barang", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, if (fotoUri != null) pink else Color.LightGray, RoundedCornerShape(16.dp))
                    .background(if (fotoUri != null) Color.Transparent else Color(0xFFFAFAFA))
                    .clickable { if (!isUploading) galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri != null) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto Barang",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ketuk untuk ganti foto", color = Color.White, fontSize = 12.sp)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ketuk untuk unggah foto", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("JPG, PNG maksimal 5MB", color = Color(0xFFBBBBBB), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── 3. FORM FIELDS ────────────────────────────────────────
            CustomTextField(label = "Nama Barang", value = namaBarang, onValueChange = { namaBarang = it }, placeholder = "Contoh: Dompet Kulit Cokelat")
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Tanggal Kejadian", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                placeholder = { Text("mm/dd/yyyy") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pink,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "No Telepon", value = noTelepon, onValueChange = { noTelepon = it }, placeholder = "089522658965")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Kategori", value = kategoriBarang, onValueChange = { kategoriBarang = it }, placeholder = "Aksesoris")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Deskripsi Detail", value = deskripsi, onValueChange = { deskripsi = it }, placeholder = "Sebutkan ciri-ciri barang...")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Lokasi Terakhir",
                value = lokasi,
                onValueChange = { lokasi = it },
                placeholder = "Contoh: Stasiun Gambir",
                trailingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Info box ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(softPink)
                    .padding(14.dp)
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = pink)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Laporan Anda akan dipublikasikan ke komunitas FindUp agar orang lain dapat membantu.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tombol Simpan ─────────────────────────────────────────
            val buttonColor = if (selectedStatus == "HILANG") Color(0xFFE53935) else Color(0xFF4CAF50)

            Button(
                onClick = {
                    isUploading = true
                    if (fotoUri != null) {
                        viewModel.uploadFoto(
                            uri = fotoUri!!,
                            onSuccess = { url ->
                                viewModel.simpanLaporan(
                                    namaBarang = namaBarang,
                                    tanggal = tanggal,
                                    noTelepon = noTelepon,
                                    kategori = kategoriBarang,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    fotoUrl = url,
                                    status = selectedStatus
                                )
                                isUploading = false
                                onBackClick()
                            },
                            onFailure = {
                                // Gagal upload foto, simpan tanpa foto
                                viewModel.simpanLaporan(
                                    namaBarang = namaBarang,
                                    tanggal = tanggal,
                                    noTelepon = noTelepon,
                                    kategori = kategoriBarang,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    fotoUrl = "",
                                    status = selectedStatus
                                )
                                isUploading = false
                                onBackClick()
                            }
                        )
                    } else {
                        viewModel.simpanLaporan(
                            namaBarang = namaBarang,
                            tanggal = tanggal,
                            noTelepon = noTelepon,
                            kategori = kategoriBarang,
                            deskripsi = deskripsi,
                            lokasi = lokasi,
                            fotoUrl = "",
                            status = selectedStatus
                        )
                        isUploading = false
                        onBackClick()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Mengunggah foto...", color = Color.White, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(
                        text = if (selectedStatus == "HILANG") "Laporkan Kehilangan" else "Laporkan Temuan",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder) },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE8737A),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            singleLine = true
        )
    }
}