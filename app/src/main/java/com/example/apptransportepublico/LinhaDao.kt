package com.example.apptransportepublico

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface LinhaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLinhaAutocarro(linhaAutocarro: LinhaAutocarro)

    @Query("SELECT * FROM linha_Autocarro")
    fun getLinhaAutocarroLiveData(): LiveData<List<LinhaAutocarro>> // LiveData version

    @Delete
    suspend fun deleteLinhaAutocarro(linhaAutocarro: LinhaAutocarro)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLinhaMetro(linhaMetro: LinhaMetro)

    @Query ("SELECT * FROM linha_metro")
    fun getLinhaMetroLiveData(): LiveData<List<LinhaMetro>>

    @Delete
    suspend fun deleteLinhaMetro(linhaMetro: LinhaMetro)
}


