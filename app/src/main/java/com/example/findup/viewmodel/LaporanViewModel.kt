package com.example.findup.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.findup.data.Laporan
import com.example.findup.data.LaporanDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class LaporanViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = LaporanDatabase.getDatabase(application).laporanDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun getAllLaporan(): Flow<List<Laporan>> {
        return dao.getAllLaporan()
    }

    fun getLaporanByUser(): Flow<List<Laporan>> {
        val userId = auth.currentUser?.uid ?: ""
        return dao.getLaporanByUser(userId)
    }

    fun uploadFoto(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: "unknown"
        val fileName = "laporan/$userId/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }.addOnFailureListener {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun simpanLaporan(
        namaBarang: String,
        tanggal: String,
        noTelepon: String,
        kategori: String,
        deskripsi: String,
        lokasi: String,
        fotoUrl: String = "",
        status: String = "HILANG"
    ) {
        val userId = auth.currentUser?.uid ?: ""

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("username") ?: "User"
                val laporan = Laporan(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    username = username,
                    namaBarang = namaBarang,
                    tanggal = tanggal,
                    noTelepon = noTelepon,
                    kategori = kategori,
                    deskripsi = deskripsi,
                    lokasi = lokasi,
                    fotoUrl = fotoUrl,
                    status = status,
                    isSynced = false
                )
                viewModelScope.launch {
                    dao.insertLaporan(laporan)
                    syncToFirebase(laporan)
                }
            }
    }

    // Offline-first: simpan ke Room dulu, sync ke Firestore kalau online
    fun simpanLaporanOfflineFirst(
        namaBarang: String,
        tanggal: String,
        noTelepon: String,
        kategori: String,
        deskripsi: String,
        lokasi: String,
        fotoUri: android.net.Uri? = null,
        status: String = "HILANG",
        onDone: () -> Unit = {}
    ) {
        val userId = auth.currentUser?.uid ?: ""

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("username") ?: "User"
                val laporan = Laporan(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    username = username,
                    namaBarang = namaBarang,
                    tanggal = tanggal,
                    noTelepon = noTelepon,
                    kategori = kategori,
                    deskripsi = deskripsi,
                    lokasi = lokasi,
                    fotoUrl = fotoUri?.toString() ?: "",
                    status = status,
                    isSynced = false
                )
                viewModelScope.launch {
                    // ✅ Simpan ke Room dulu — langsung muncul di Laporanku
                    dao.insertLaporan(laporan)
                    onDone()

                    // ✅ Coba sync ke Firestore (upload foto dulu kalau ada)
                    if (fotoUri != null) {
                        uploadFoto(
                            uri = fotoUri,
                            onSuccess = { url ->
                                val updated = laporan.copy(fotoUrl = url)
                                viewModelScope.launch { dao.updateLaporan(updated) }
                                syncToFirebase(updated)
                            },
                            onFailure = {
                                // Gagal upload foto, sync tanpa foto
                                syncToFirebase(laporan.copy(fotoUrl = ""))
                            }
                        )
                    } else {
                        syncToFirebase(laporan)
                    }
                }
            }
            .addOnFailureListener {
                // Offline — simpan ke Room saja dulu
                val username = auth.currentUser?.displayName ?: "User"
                val laporan = Laporan(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    username = username,
                    namaBarang = namaBarang,
                    tanggal = tanggal,
                    noTelepon = noTelepon,
                    kategori = kategori,
                    deskripsi = deskripsi,
                    lokasi = lokasi,
                    fotoUrl = fotoUri?.toString() ?: "",
                    status = status,
                    isSynced = false
                )
                viewModelScope.launch {
                    dao.insertLaporan(laporan)
                    onDone()
                }
            }
    }

    fun hapusLaporan(laporan: Laporan) {
        viewModelScope.launch {
            dao.deleteLaporan(laporan)
            firestore.collection("laporan").document(laporan.id).delete()
        }
    }

    fun getLaporanById(id: String, onResult: (Laporan?) -> Unit) {
        firestore.collection("laporan").document(id).get()
            .addOnSuccessListener { doc ->
                val laporan = doc.toObject(Laporan::class.java)
                onResult(laporan)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateLaporan(
        id: String,
        namaBarang: String,
        tanggal: String,
        noTelepon: String,
        kategori: String,
        deskripsi: String,
        lokasi: String,
        fotoUrl: String
    ) {
        val updates = mapOf(
            "namaBarang" to namaBarang,
            "tanggal" to tanggal,
            "noTelepon" to noTelepon,
            "kategori" to kategori,
            "deskripsi" to deskripsi,
            "lokasi" to lokasi,
            "fotoUrl" to fotoUrl
        )
        firestore.collection("laporan").document(id).update(updates)

        firestore.collection("laporan").document(id).get()
            .addOnSuccessListener { doc ->
                val laporan = doc.toObject(Laporan::class.java)
                laporan?.let {
                    val updated = it.copy(
                        namaBarang = namaBarang, tanggal = tanggal,
                        noTelepon = noTelepon, kategori = kategori,
                        deskripsi = deskripsi, lokasi = lokasi, fotoUrl = fotoUrl
                    )
                    viewModelScope.launch { dao.updateLaporan(updated) }
                }
            }
    }

    private fun syncToFirebase(laporan: Laporan) {
        firestore.collection("laporan")
            .document(laporan.id)
            .set(laporan)
            .addOnSuccessListener {
                viewModelScope.launch {
                    dao.updateLaporan(laporan.copy(isSynced = true))
                }
            }
    }

    fun fetchAllLaporanFromFirestore(onResult: (List<Laporan>) -> Unit) {
        firestore.collection("laporan")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Laporan::class.java)
                } ?: emptyList()
                onResult(list)
            }
    }

    // ✅ FIXED — set metadata dulu, baru add pesan
    fun kirimPesan(chatId: String, senderId: String, receiverId: String, text: String) {
        val timestamp = System.currentTimeMillis()

        android.util.Log.d("Chat", "kirimPesan dipanggil")
        android.util.Log.d("Chat", "chatId: $chatId")
        android.util.Log.d("Chat", "senderId: $senderId")
        android.util.Log.d("Chat", "receiverId: $receiverId")

        val meta = hashMapOf(
            "participants" to listOf(senderId, receiverId),
            "lastMessage" to text,
            "lastTimestamp" to timestamp,
            "lastSenderId" to senderId
        )

        firestore.collection("chats")
            .document(chatId)
            .set(meta)
            .addOnSuccessListener {
                android.util.Log.d("Chat", "metadata berhasil disimpan")
                val pesan = hashMapOf(
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "text" to text,
                    "timestamp" to timestamp,
                    "isRead" to false
                )
                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .add(pesan)
                    .addOnSuccessListener {
                        android.util.Log.d("Chat", "pesan berhasil disimpan")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("Chat", "gagal simpan pesan: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Chat", "gagal simpan metadata: ${e.message}")
            }
    }

    fun dengarPesan(chatId: String, onUpdate: (List<Map<String, Any>>) -> Unit) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                onUpdate(list)
            }
    }

    // Tandai semua pesan sudah dibaca
    fun tandaiSudahDibaca(chatId: String, userId: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    doc.reference.update("isRead", true)
                }
            }
    }

    // Hitung pesan belum dibaca per chat
    fun hitungPesanBelumDibaca(chatId: String, userId: String, onResult: (Int) -> Unit) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, _ ->
                onResult(snapshot?.size() ?: 0)
            }
    }

    // ✅ FIXED — hapus orderBy, sort di client side
    fun getInboxChats(userId: String, onUpdate: (List<Map<String, Any>>) -> Unit) {
        firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    android.util.Log.e("Inbox", "Error getInboxChats: ${error.message}")
                    return@addSnapshotListener
                }
                val list = snapshots?.documents
                    ?.mapNotNull { doc ->
                        doc.data?.toMutableMap()?.also { it["chatId"] = doc.id }
                    }
                    ?.sortedByDescending { map ->
                        when (val ts = map["lastTimestamp"]) {
                            is Long   -> ts
                            is Double -> ts.toLong()
                            else      -> 0L
                        }
                    }
                    ?: emptyList()
                onUpdate(list)
            }
    }
}