package com.example.findup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Laporan::class], version = 2, exportSchema = false)
abstract class LaporanDatabase : RoomDatabase() {

    abstract fun laporanDao(): LaporanDao

    companion object {
        @Volatile
        private var INSTANCE: LaporanDatabase? = null

        fun getDatabase(context: Context): LaporanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaporanDatabase::class.java,
                    "laporan_database"
                )
                    .fallbackToDestructiveMigration() // ← ini yang penting
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}