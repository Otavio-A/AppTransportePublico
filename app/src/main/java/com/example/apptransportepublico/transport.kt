package com.example.apptransportepublico

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transport_data")
data class TransportData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated primary key
    @ColumnInfo(name = "linha") val linha: String,   // e.g., "Linha 204"
    @ColumnInfo(name = "latitude") val latitude: Double,   // Latitude coordinate
    @ColumnInfo(name = "longitude") val longitude: Double, // Longitude coordinate
)
