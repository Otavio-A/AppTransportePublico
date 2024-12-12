package com.example.apptransportepublico

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo



@Entity(tableName = "linha_Autocarro")
class LinhaAutocarro {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "linhaAutocarroId")
    var id: Int = 0

    @ColumnInfo(name = "nomeProduto")
    var linha: String

    constructor() : this("")

    constructor(nomeProduto: String) {
        this.linha = nomeProduto
    }
}