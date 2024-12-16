package com.example.apptransportepublico

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class LinhaAutocarroRepository(private val linhaAutocarroDao: LinhaAutocarroDao) {
    val allLinhas: LiveData<List<LinhaAutocarro>> = linhaAutocarroDao.getLinhaAutocarroLiveData()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertLinha(linha: LinhaAutocarro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaAutocarroDao.insertLinhaAutocarro(linha)
        }
    }

    fun deleteLinha(linha: LinhaAutocarro) {
        coroutineScope.launch(Dispatchers.IO) {
            linhaAutocarroDao.deleteLinhaAutocarro(linha)
        }
    }
}