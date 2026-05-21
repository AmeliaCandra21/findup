package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.findup.viewmodel.LaporanViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    navController: NavController,
    viewModel: LaporanViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val firestore = FirebaseFirestore.getInstance()

    var inboxList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var contactNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    // ✅ Map chatId -> unread count
    var unreadCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.getInboxChats(currentUserId) { list ->
                inboxList = list

                list.forEach { chat ->
                    val participants = chat["participants"] as? List<*> ?: return@forEach
                    val contactId = participants.firstOrNull { it != currentUserId } as? String ?: return@forEach
                    val chatId = chat["chatId"] as? String ?: return@forEach

                    // Ambil nama kontak
                    firestore.collection("users").document(contactId).get()
                        .addOnSuccessListener { doc ->
                            val username = doc.getString("username") ?: "User"
                            contactNames = contactNames + (contactId to username)
                        }

                    // ✅ Hitung unread per chat
                    viewModel.hitungPesanBelumDibaca(chatId, currentUserId) { count ->
                        unreadCounts = unreadCounts + (chatId to count)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        topBar = {
            TopAppBar(
                title = {
                    Text("Pesan", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali",
                            tint = Color(0xFF1A1A2E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
            HorizontalDivider(color = Color(0xFFF0F0F0))
        }
    ) { innerPadding ->
        if (inboxList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Belum ada pesan",
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E))
                    Spacer(Modifier.height(6.dp))
                    Text("Pesan masuk akan muncul di sini",
                        fontSize = 13.sp, color = Color(0xFF888888))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(inboxList) { chat ->
                    val chatId = chat["chatId"] as? String ?: ""
                    val participants = chat["participants"] as? List<*> ?: emptyList<String>()
                    val contactId = participants.firstOrNull { it != currentUserId } as? String ?: ""
                    val lastMessage = chat["lastMessage"] as? String ?: ""
                    val lastTimestamp = when (val ts = chat["lastTimestamp"]) {
                        is Long   -> ts
                        is Double -> ts.toLong()
                        else      -> 0L
                    }
                    val timeStr = SimpleDateFormat("HH.mm", Locale.getDefault()).format(Date(lastTimestamp))
                    val contactName = contactNames[contactId] ?: "..."
                    val unreadCount = unreadCounts[chatId] ?: 0  // ✅

                    InboxItem(
                        contactName = contactName,
                        lastMessage = lastMessage,
                        time        = timeStr,
                        unreadCount = unreadCount,  // ✅
                        onClick     = {
                            navController.navigate("ChatDariInbox/$contactId/$contactName")
                        }
                    )
                    HorizontalDivider(
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxItem(
    contactName : String,
    lastMessage : String,
    time        : String,
    unreadCount : Int,      // ✅
    onClick     : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape)
                .background(Color(0xFFFFCDD2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contactName.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8737A)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contactName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = lastMessage,
                fontSize = 13.sp,
                color = if (unreadCount > 0) Color(0xFF1A1A2E) else Color(0xFF888888),  // ✅ bold kalau ada unread
                fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // ✅ Badge unread count
        Column(horizontalAlignment = Alignment.End) {
            Text(time, fontSize = 11.sp,
                color = if (unreadCount > 0) Color(0xFFE8737A) else Color(0xFF888888))
            Spacer(Modifier.height(4.dp))
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8737A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}