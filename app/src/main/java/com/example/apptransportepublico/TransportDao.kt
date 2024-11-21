package com.example.apptransportepublico

import androidx.room.*

@Dao
interface BusLineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBusLine(busLine: BusLine)

    @Query("SELECT * FROM bus_lines WHERE linha = :linha")
    suspend fun getBusLine(linha: String): BusLine?

    @Query("SELECT * FROM bus_lines")
    suspend fun getAllBusLines(): List<BusLine>

    @Delete
    suspend fun deleteBusLine(busLine: BusLine)

    @Query("DELETE FROM bus_lines")
    suspend fun clearAllBusLines()
}
