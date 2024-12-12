package com.example.apptransportepublico

import androidx.room.*
// TODO Completar o DAO
@Dao
interface LinhaAutocarroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBusLine(linhaAutocarro: LinhaAutocarro)

    @Query("SELECT * FROM linha_Autocarro linha = :linha")
    suspend fun getBusLine(linha: String): LinhaAutocarro?

    @Query("SELECT * FROM linha_Autocarro")
    suspend fun getAllBusLines(): List<LinhaAutocarro>

    @Delete
    suspend fun deleteBusLine(linhaAutocarro: LinhaAutocarro)

    @Query("DELETE FROM linha_Autocarro")
    suspend fun clearAllBusLines()
}
