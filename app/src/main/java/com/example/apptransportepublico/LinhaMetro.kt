package com.example.apptransportepublico

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linha_Metro")
data class LinhaMetro(
    @PrimaryKey val linha: String
)