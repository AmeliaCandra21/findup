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

    // Ambil semua laporan (feed home - semua user)
    fun getAllLaporan(): Flow<List<Laporan>> {
        return dao.getAllLaporan()
    }

    // Ambil laporan milik user sendiri (LaporanScreen)
    fun getLaporanByUser(): Flow<List<Laporan>> {
        val userId = auth.currentUser?.uid ?: ""
        return dao.getLaporanByUser(userId)
    }

    // Upload foto ke Firebase Storage, dapat URL lewat callback
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

    // Simpan laporan — ambil username dulu dari Firestore
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

    // Hapus laporan
    fun hapusLaporan(laporan: Laporan) {
        viewModelScope.launch {
            dao.deleteLaporan(laporan)
            firestore.collection("laporan").document(laporan.id).delete()
        }
    }

    // Ambil laporan by ID (untuk DetailBarang)
    fun getLaporanById(id: String, onResult: (Laporan?) -> Unit) {
        firestore.collection("laporan").document(id).get()
            .addOnSuccessListener { doc ->
                val laporan = doc.toObject(Laporan::class.java)
                onResult(laporan)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Update laporan
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

    // Sync ke Firebase
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

    // Ambil semua laporan dari Firestore (real-time feed)
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
    // Kirim pesan ke Firestore
    fun kirimPesan(chatId: String, senderId: String, text: String) {
        val pesan = mapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(pesan)
    }

    // Dengarkan pesan real-time
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
}