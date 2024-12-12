package com.example.apptransportepublico

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface LinhaAutocarroDao {
    @Insert
    suspend fun insertLinhaAutocarro(linhaAutocarro: LinhaAutocarro)

    @Query("SELECT * FROM linha_Autocarro")
    fun getLinhaAutocarroLiveData(): LiveData<List<LinhaAutocarro>> // LiveData version

    @Delete
    suspend fun deleteLinhaAutocarro(linhaAutocarro: LinhaAutocarro)
}


