package com.rockstar.sensor

import android.content.Context
import androidx.room.Room

object DatabaseManager {
    private var appDatabase: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (appDatabase == null) {
            synchronized(DatabaseManager::class.java) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "orientation_db"
                    ).build()
                }
            }
        }
        return appDatabase!!
    }
}
