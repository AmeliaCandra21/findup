package com.example.app.ui.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─── Warna ────────────────────────────────────────────────────────────────────
private val PinkPrimary       = Color(0xFFF08080)
private val PinkBubble        = Color(0xFFF4A0A0)
private val PinkLight         = Color(0xFFFFF0F0)
private val PinkInputBg       = Color(0xFFFFF0F0)
private val ReceiverBubble    = Color.White
private val BackgroundColor   = Color(0xFFFAFAFA)
private val TextPrimary       = Color(0xFF212121)
private val TextSecondary     = Color(0xFF9E9E9E)
private val TopBarColor       = Color.White

// ─── Model ────────────────────────────────────────────────────────────────────
enum class MessageType { SENT, RECEIVED }

data class ChatMessage(
    val id        : String = UUID.randomUUID().toString(),
    val text      : String,
    val type      : MessageType,
    val time      : String = SimpleDateFormat("HH.mm", Locale.getDefault()).format(Date()),
    val isRead    : Boolean = true
)

data class ItemBaanTemuan(
    val imageUrl  : String? = null,
    val title     : String,
    val desc      : String
)

// ─── Halaman Chat ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactName   : String         = "Joko Ramadhan",
    contactPhoto  : String?        = null,
    barangTemuan  : ItemBaanTemuan? = ItemBaanTemuan(
        title    = "Tentang Barang Temuan",
        desc     = "Dompet hitam merk widodo asolole njiwan vario"
    ),
    onBack        : () -> Unit = {}
) {
    // ── State ──────────────────────────────────────────────────────────────
    val initialMessages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Halo! Saya dengan Joko Ramadhan yang tadi post tentang dompet di madiun itu punya saya keknya ,hehe.",
                type = MessageType.RECEIVED,
                time = "08.10"
            ),
            ChatMessage(
                text = "Halo Joko!Oh iya benar, itu punya saya. Terimakasih banyak karena kamu sudah mengamankanya.tadi tertinggal di pasar kewan",
                type = MessageType.SENT,
                time = "08.10"
            ),
            ChatMessage(
                text = "Siap kak,Rencana mau amil kapan ya soalnya saya lagi sibuk acara ini,maklum orang IT loh ya",
                type = MessageType.RECEIVED,
                time = "08.10"
            )
        )
    }

    var inputText     by remember { mutableStateOf("") }
    val listState     = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto scroll ke bawah saat pesan baru masuk
    LaunchedEffect(initialMessages.size) {
        if (initialMessages.isNotEmpty()) {
            listState.animateScrollToItem(initialMessages.size - 1)
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            ChatTopBar(
                name      = contactName,
                photoUrl  = contactPhoto,
                onBack    = onBack
            )
        },
        bottomBar = {
            ChatInputBar(
                value    = inputText,
                onValueChange = { inputText = it },
                onSend   = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        initialMessages.add(
                            ChatMessage(text = text, type = MessageType.SENT)
                        )
                        inputText = ""
                        coroutineScope.launch {
                            listState.animateScrollToItem(initialMessages.size - 1)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state          = listState,
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Kartu barang temuan di atas (hanya muncul sekali)
            if (barangTemuan != null) {
                item {
                    BarangTemuanCard(item = barangTemuan)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Daftar pesan
            items(initialMessages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    name     : String,
    photoUrl : String?,
    onBack   : () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar kontak
                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCDD2)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model              = photoUrl,
                            contentDescription = name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text       = name.first().toString(),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = PinkPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = name,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }
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
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector        = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint               = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
    )
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
}

// ─── Kartu Barang Temuan ──────────────────────────────────────────────────────
@Composable
private fun BarangTemuanCard(item: ItemBaanTemuan) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = PinkLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail barang
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD7B899)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📦", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text       = item.title,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = PinkPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text     = item.desc,
                    fontSize = 13.sp,
                    color    = TextPrimary
                )
            }
        }
    }
}

// ─── Bubble Chat ──────────────────────────────────────────────────────────────
@Composable
private fun ChatBubble(message: ChatMessage) {
    val isSent    = message.type == MessageType.SENT
    val bubbleColor = if (isSent) PinkBubble else ReceiverBubble
    val textColor   = if (isSent) Color.White else TextPrimary
    val alignment   = if (isSent) Alignment.End else Alignment.Start

    val bubbleShape = if (isSent) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text      = message.text,
                fontSize  = 14.sp,
                color     = textColor,
                textAlign = if (isSent) TextAlign.Justify else TextAlign.Justify,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text     = message.time,
            fontSize = 11.sp,
            color    = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

// ─── Input Bar ────────────────────────────────────────────────────────────────
@Composable
private fun ChatInputBar(
    value         : String,
    onValueChange : (String) -> Unit,
    onSend        : () -> Unit
) {
    Surface(
        color     = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Field teks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PinkInputBg)
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text     = "Ketik Pesan...",
                        fontSize = 14.sp,
                        color    = TextSecondary
                    )
                }
                BasicTextField(
                    value         = value,
                    onValueChange = onValueChange,
                    textStyle     = TextStyle(fontSize = 14.sp, color = TextPrimary),
                    modifier      = Modifier.fillMaxWidth(),
                    maxLines      = 4
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Tombol kirim
            FloatingActionButton(
                onClick            = onSend,
                modifier           = Modifier.size(46.dp),
                shape              = CircleShape,
                containerColor     = PinkPrimary,
                contentColor       = Color.White,
                elevation          = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Send,
                    contentDescription = "Kirim",
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChatScreen()
    }
}