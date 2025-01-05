package com.example.apptransportepublico

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [LinhaAutocarro::class, LinhaMetro::class], version = 2, exportSchema = false)
abstract class LinhaDatabase : RoomDatabase() {
    abstract fun linhaDao(): LinhaDao

    companion object {
        private var INSTANCE: LinhaDatabase? = null

        fun getInstance(context: Context): LinhaDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LinhaDatabase::class.java,
                        "linha_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}