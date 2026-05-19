package com.example.findup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.findup.data.Laporan
import com.example.findup.data.LaporanDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class LaporanViewModel (application: Application) : AndroidViewModel(application) {
    private val dao = LaporanDatabase.getDatabase(application).laporanDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //Ambil semua laporan milik user yang login
    fun getLaporanByUser() : Flow<List<Laporan>> {
        val userId = auth.currentUser?.uid ?: ""
        return dao.getLaporanByUser(userId)
    }

    //Simpan laporan ke Room
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
        val laporan = Laporan(
            id = UUID.randomUUID().toString(),
            userId = userId,
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
            // Simpan ke Room
            dao.insertLaporan(laporan)
            // Sync ke Firebase
            syncToFirebase(laporan)
        }
    }

    // Hapus laporan
    fun hapusLaporan(laporan: Laporan) {
        viewModelScope.launch {
            dao.deleteLaporan(laporan)
            firestore.collection("laporan").document(laporan.id).delete()
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
}