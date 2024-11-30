package com.example.apptransportepublico

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bus_lines")
data class BusLine(
    @PrimaryKey val linha: String
)