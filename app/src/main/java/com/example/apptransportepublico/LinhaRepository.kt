package com.example.apptransportepublico

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class LinhaRepository(private val linhaDao: LinhaDao) {
    val allAutocarros: LiveData<List<LinhaAutocarro>> = linhaDao.getLinhaAutocarroLiveData()
    val allMetros: LiveData<List<LinhaMetro>> = linhaDao.getLinhaMetroLiveData()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertAutocarro(linha: LinhaAutocarro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaDao.insertLinhaAutocarro(linha)
        }
    }

    fun deleteAutocarro(linha: LinhaAutocarro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaDao.deleteLinhaAutocarro(linha)
        }
    }

    fun insertMetro(linha: LinhaMetro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaDao.insertLinhaMetro(linha)
        }
    }

    fun deleteMetro(linha: LinhaMetro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaDao.deleteLinhaMetro(linha)
        }
    }
}
