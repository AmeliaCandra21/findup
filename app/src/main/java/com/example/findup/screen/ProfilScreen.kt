package com.example.findup.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {

    val context     = LocalContext.current
    val auth        = FirebaseAuth.getInstance()
    val db          = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser
    val uid         = currentUser?.uid ?: ""

    // ── State ──────────────────────────────────────────────────────────────────
    var username         by remember { mutableStateOf("") }
    var photoUrl         by remember { mutableStateOf<String?>(null) }
    var isLoading        by remember { mutableStateOf(true) }
    var isEditingName    by remember { mutableStateOf(false) }
    var editedUsername   by remember { mutableStateOf("") }
    var isSavingName     by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val pinkPrimary = Color(0xFFEFA7A9)
    val pinkLight   = Color(0xFFFFE4E4)
    val pinkSurface = Color(0xFFFFF5F5)

    // ── Ambil data dari Firestore ──────────────────────────────────────────────
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    username  = doc.getString("username") ?: ""
                    photoUrl  = doc.getString("photoUrl")
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    // ── Helper: decode Base64 ke Bitmap ───────────────────────────────────────
    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val data  = base64String.substringAfter("base64,")
            val bytes = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    // ── Picker foto dari galeri (simpan Base64 ke Firestore) ──────────────────
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && uid.isNotEmpty()) {
            isUploadingPhoto = true
            try {
                val inputStream    = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                // Fix rotasi
                val inputStream2 = context.contentResolver.openInputStream(uri)
                val exif = androidx.exifinterface.media.ExifInterface(inputStream2!!)
                val rotation = exif.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = android.graphics.Matrix()
                when (rotation) {
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90  -> matrix.postRotate(90f)
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
                val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                val size = minOf(rotatedBitmap.width, rotatedBitmap.height)
                val x = (rotatedBitmap.width - size) / 2
                val y = (rotatedBitmap.height - size) / 2
                val cropped = Bitmap.createBitmap(rotatedBitmap, x, y, size, size)
                val resized = Bitmap.createScaledBitmap(cropped, 300, 300, true)

                val outputStream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                val dataUri = "data:image/jpeg;base64,$base64String"

                db.collection("users").document(uid)
                    .update("photoUrl", dataUri)
                    .addOnSuccessListener {
                        photoUrl         = dataUri
                        isUploadingPhoto = false
                        Toast.makeText(context, "Foto berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        isUploadingPhoto = false
                        Toast.makeText(context, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception) {
                isUploadingPhoto = false
                Toast.makeText(context, "Gagal memproses foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Dialog konfirmasi logout ───────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari Akun", fontWeight = FontWeight.SemiBold) },
            text  = { Text("Kamu yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        showLogoutDialog = false
                        navController?.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                ) {
                    Text("Keluar", color = pinkPrimary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ── UI Utama ───────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = pinkSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Profil Saya",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF212121)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pinkSurface)
            )
        }
    ) { innerPadding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = pinkPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Kartu Header ─────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Banner gradient
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(pinkLight, pinkPrimary.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(modifier = Modifier.offset(y = 40.dp)) {

                            // Lingkaran putih border avatar
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                val currentPhoto = photoUrl
                                when {
                                    // Base64
                                    currentPhoto?.startsWith("data:image") == true -> {
                                        val bitmap = decodeBase64ToBitmap(currentPhoto)
                                        if (bitmap != null) {
                                            Image(
                                                bitmap             = bitmap.asImageBitmap(),
                                                contentDescription = "Foto Profil",
                                                contentScale       = ContentScale.Crop,
                                                modifier           = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            AvatarPlaceholder(pinkLight, pinkPrimary)
                                        }
                                    }
                                    // URL biasa
                                    !currentPhoto.isNullOrBlank() -> {
                                        AsyncImage(
                                            model              = currentPhoto,
                                            contentDescription = "Foto Profil",
                                            contentScale       = ContentScale.Crop,
                                            modifier           = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                    // Placeholder
                                    else -> {
                                        AvatarPlaceholder(pinkLight, pinkPrimary)
                                    }
                                }
                            }

                            // Tombol kamera
                            Box(
                                modifier         = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(pinkPrimary)
                                    .align(Alignment.BottomEnd)
                                    .clickable(enabled = !isUploadingPhoto) {
                                        photoPicker.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUploadingPhoto) {
                                    CircularProgressIndicator(
                                        modifier    = Modifier.size(14.dp),
                                        color       = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector        = Icons.Default.CameraAlt,
                                        contentDescription = "Ganti Foto",
                                        tint               = Color.White,
                                        modifier           = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(52.dp))

                    // ── Username (bisa diedit) ───────────────────────────────
                    if (isEditingName) {
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value         = editedUsername,
                                onValueChange = { editedUsername = it },
                                modifier      = Modifier.weight(1f),
                                singleLine    = true,
                                shape         = RoundedCornerShape(10.dp),
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = pinkPrimary,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                label = { Text("Username") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (editedUsername.isBlank()) {
                                        Toast.makeText(context, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                        return@IconButton
                                    }
                                    isSavingName = true
                                    db.collection("users").document(uid)
                                        .update("username", editedUsername.trim())
                                        .addOnSuccessListener {
                                            username      = editedUsername.trim()
                                            isEditingName = false
                                            isSavingName  = false
                                            Toast.makeText(context, "Username diperbarui", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            isSavingName = false
                                            Toast.makeText(context, "Gagal memperbarui username", Toast.LENGTH_SHORT).show()
                                        }
                                },
                                enabled = !isSavingName
                            ) {
                                if (isSavingName) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color    = pinkPrimary
                                    )
                                } else {
                                    Icon(Icons.Default.Check, contentDescription = "Simpan", tint = pinkPrimary)
                                }
                            }

                            IconButton(onClick = { isEditingName = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Batal", tint = Color.Gray)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text       = username.ifBlank { "-" },
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color(0xFF212121)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Edit Username",
                                tint               = pinkPrimary,
                                modifier           = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        editedUsername = username
                                        isEditingName  = true
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // ── Kartu Info Akun ──────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text       = "Informasi Akun",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Username", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                        Text(
                            text       = username.ifBlank { "-" },
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color(0xFF212121)
                        )
                    }
                }
            }

            // ── Tombol Logout ────────────────────────────────────────────────
            OutlinedButton(
                onClick  = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = pinkPrimary),
                border   = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    pinkPrimary.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector        = Icons.Default.ExitToApp,
                    contentDescription = "Keluar",
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Keluar dari Akun",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── Komponen Avatar Placeholder ───────────────────────────────────────────────
@Composable
private fun AvatarPlaceholder(pinkLight: Color, pinkPrimary: Color) {
    Box(
        modifier         = Modifier
            .size(74.dp)
            .clip(CircleShape)
            .background(pinkLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.Default.Person,
            contentDescription = null,
            tint               = pinkPrimary,
            modifier           = Modifier.size(40.dp)
        )
    }
}