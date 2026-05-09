package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findup.ui.theme.*

// ─── Warna tema ───────────────────────────────────────────────────────────────
//private val PinkPrimary   = Color(0xFFE57373)
//private val PinkLight     = Color(0xFFFFCDD2)
//private val PinkSurface   = Color(0xFFFFF5F5)
//private val TextPrimary   = Color(0xFF212121)
//private val TextSecondary = Color(0xFF757575)
//private val DividerColor  = Color(0xFFEEEEEE)
//private val White         = Color.White

// ─── Data model ───────────────────────────────────────────────────────────────
data class UserProfile(
    val name: String,
    val memberSince: String,
    val email: String,
    val phone: String,
    val photoUrl: String? = null
)

// ─── Halaman Profil ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile = UserProfile(
        name        = "Awa Paxley",
        memberSince = "Jan 2025",
        email       = "budi.s@email.com",
        phone       = "0812-3456-7890"
    ),
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        containerColor = PinkSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Profil",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint               = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkSurface
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Kartu header profil ──────────────────────────────────────────
            ProfileHeaderCard(profile = profile)

            // ── Kartu info kontak ────────────────────────────────────────────
            ContactInfoRow(email = profile.email, phone = profile.phone)

            // ── Tombol keluar ────────────────────────────────────────────────
            LogoutButton(onClick = onLogout)
        }
    }
}

// ─── Kartu Header ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileHeaderCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Banner gradasi merah muda
            Box(
                modifier          = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PinkLight, PinkPrimary.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment  = Alignment.BottomCenter
            ) {
                // Foto profil (setengah menonjol ke luar banner)
                Box(
                    modifier = Modifier.offset(y = 36.dp)
                ) {
                    ProfileAvatar(photoUrl = profile.photoUrl)
                }
            }

            Spacer(modifier = Modifier.height(44.dp)) // ruang untuk avatar

            Text(
                text       = profile.name,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "Anggota Sejak ${profile.memberSince}",
                fontSize = 13.sp,
                color    = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ─── Avatar ───────────────────────────────────────────────────────────────────
@Composable
private fun ProfileAvatar(photoUrl: String?) {
    Box(
        modifier         = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model             = photoUrl,
                contentDescription = "Foto Profil",
                contentScale      = ContentScale.Crop,
                modifier          = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
        } else {
            // Placeholder jika tidak ada foto
            Box(
                modifier         = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(PinkLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = photoUrl?.firstOrNull()?.toString() ?: "A",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PinkPrimary
                )
            }
        }
    }
}

// ─── Baris Info Kontak ────────────────────────────────────────────────────────
@Composable
private fun ContactInfoRow(email: String, phone: String) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ContactInfoCard(
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Email,
            label    = "Email",
            value    = email
        )
        ContactInfoCard(
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Phone,
            label    = "Nomor HP",
            value    = phone
        )
    }
}

@Composable
private fun ContactInfoCard(
    modifier : Modifier,
    icon     : ImageVector,
    label    : String,
    value    : String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = PinkPrimary,
                modifier           = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text       = value,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = TextPrimary
            )
        }
    }
}

// ─── Tombol Keluar ────────────────────────────────────────────────────────────
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
        border   = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
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
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────
@Composable
private fun BottomNavigationBar() {
    NavigationBar(
        containerColor = White,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick  = {},
            icon     = {
                Icon(
                    imageVector        = Icons.Default.Email, // ganti dengan icon Home
                    contentDescription = "Beranda"
                )
            },
            label    = { Text("Beranda") },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = PinkPrimary,
                selectedTextColor   = PinkPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = PinkLight
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = {},
            icon     = {
                Icon(
                    imageVector        = Icons.Default.Email, // ganti dengan icon Laporan
                    contentDescription = "Laporanku"
                )
            },
            label    = { Text("Laporanku") },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = PinkPrimary,
                selectedTextColor   = PinkPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = PinkLight
            )
        )
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = {
                Icon(
                    imageVector        = Icons.Default.Email, // ganti dengan icon Profil
                    contentDescription = "Profil"
                )
            },
            label    = { Text("Profil") },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = PinkPrimary,
                selectedTextColor   = PinkPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = PinkLight
            )
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}