package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.findup.ui.theme.*
import com.example.findup.viewmodel.LaporanViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class MessageType { SENT, RECEIVED }

data class ChatMessage(
    val id      : String = UUID.randomUUID().toString(),
    val text    : String,
    val type    : MessageType,
    val time    : String = SimpleDateFormat("HH.mm", Locale.getDefault()).format(Date()),
    val isRead  : Boolean = true
)

data class ItemBaanTemuan(
    val imageUrl : String? = null,
    val title    : String,
    val desc     : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactUserId : String          = "",
    contactName   : String          = "User",
    contactPhoto  : String?         = null,
    barangTemuan  : ItemBaanTemuan? = null,
    onBack        : () -> Unit      = {},
    viewModel     : LaporanViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // chatId unik: gabungan 2 userId diurutkan supaya selalu sama
    val chatId = remember(currentUserId, contactUserId) {
        listOf(currentUserId, contactUserId).sorted().joinToString("_")
    }

    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Dengarkan pesan real-time dari Firestore
    LaunchedEffect(chatId) {
        viewModel.dengarPesan(chatId) { rawList ->
            messages = rawList.map { data ->
                val senderId = data["senderId"] as? String ?: ""
                val text = data["text"] as? String ?: ""
                val timestamp = data["timestamp"] as? Long ?: 0L
                val time = SimpleDateFormat("HH.mm", Locale.getDefault()).format(Date(timestamp))
                ChatMessage(
                    id   = "$senderId-$timestamp",
                    text = text,
                    type = if (senderId == currentUserId) MessageType.SENT else MessageType.RECEIVED,
                    time = time
                )
            }
        }
    }

    // Auto scroll ke bawah saat pesan baru
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            ChatTopBar(name = contactName, photoUrl = contactPhoto, onBack = onBack)
        },
        bottomBar = {
            ChatInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    val text = inputText.trim()
                    if (text.isNotEmpty() && currentUserId.isNotEmpty()) {
                        viewModel.kirimPesan(chatId, currentUserId, text)
                        inputText = ""
                        coroutineScope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (messages.isEmpty()) {
            // Empty state chat
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (barangTemuan != null) {
                        BarangTemuanCard(item = barangTemuan)
                        Spacer(Modifier.height(24.dp))
                    }
                    Text("💬", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Belum ada pesan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Mulai percakapan dengan $contactName",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (barangTemuan != null) {
                    item {
                        BarangTemuanCard(item = barangTemuan)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(name: String, photoUrl: String?, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFFFCDD2)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl, contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = name.firstOrNull()?.toString() ?: "?",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PinkPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
    )
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
}

@Composable
private fun BarangTemuanCard(item: ItemBaanTemuan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PinkLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD7B899)),
                contentAlignment = Alignment.Center
            ) { Text("📦", fontSize = 22.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PinkPrimary)
                Spacer(modifier = Modifier.height(3.dp))
                Text(item.desc, fontSize = 13.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isSent = message.type == MessageType.SENT
    val bubbleColor = if (isSent) PinkBubble else ReceiverBubble
    val textColor = if (isSent) Color.White else TextPrimary
    val alignment = if (isSent) Alignment.End else Alignment.Start
    val bubbleShape = if (isSent) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier.widthIn(max = 260.dp).clip(bubbleShape)
                .background(bubbleColor).padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(message.text, fontSize = 14.sp, color = textColor, lineHeight = 20.sp)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(message.time, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp))
                    .background(PinkInputBg).padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                if (value.isEmpty()) {
                    Text("Ketik Pesan...", fontSize = 14.sp, color = TextSecondary)
                }
                BasicTextField(
                    value = value, onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary),
                    modifier = Modifier.fillMaxWidth(), maxLines = 4
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            FloatingActionButton(
                onClick = onSend, modifier = Modifier.size(46.dp),
                shape = CircleShape, containerColor = PinkPrimary, contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Kirim", modifier = Modifier.size(20.dp))
            }
        }
    }
}