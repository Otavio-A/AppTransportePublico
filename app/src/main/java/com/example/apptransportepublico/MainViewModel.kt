package com.example.apptransportepublico

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LinhaAutocarroRepository
    val allLinhas: LiveData<List<LinhaAutocarro>>

    init {
        val linhaAutocarroDao = LinhaDatabase.getInstance(application).linhaAutocarroDao()
        repository = LinhaAutocarroRepository(linhaAutocarroDao)
        allLinhas = repository.allLinhas
    }

    fun insertLinha(linha: LinhaAutocarro) {
        repository.insertLinha(linha)
    }

    fun deleteLinha(linha: LinhaAutocarro) {
        repository.deleteLinha(linha)
    }
}