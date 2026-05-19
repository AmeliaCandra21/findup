package com.example.findup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan")
data class Laporan(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val namaBarang: String = "",
    val tanggal: String = "",
    val noTelepon: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val lokasi: String = "",
    val fotoUrl: String = "",
    val status: String = "HILANG",
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)