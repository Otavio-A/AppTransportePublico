package com.example.apptransportepublico

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo



@Entity(tableName = "linha_Autocarro")
data class LinhaAutocarro (
    @PrimaryKey val linha: String   // Eu realmente so preciso da linha
)