package com.example.findup.data

import androidx.room.Dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaporan(laporan: Laporan)

    @Update
    suspend fun updateLaporan(laporan: Laporan)

    @Delete
    suspend fun deleteLaporan(laporan: Laporan)

    @Query("SELECT * FROM laporan ORDER BY createdAt DESC")
    fun getAllLaporan(): Flow<List<Laporan>>

    @Query("SELECT * FROM laporan WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLaporanByUser(userId: String): Flow<List<Laporan>>

    @Query("SELECT * FROM laporan WHERE isSynced = 0")
    suspend fun getUnsyncedLaporan(): List<Laporan>
}