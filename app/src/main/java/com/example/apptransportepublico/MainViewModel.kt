package com.example.apptransportepublico

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LinhaAutocarroRepository
    val allLinhas: LiveData<List<LinhaAutocarro>>
    val linhaSelecionada = MutableLiveData<String>()
    /*
    Já que eu quero que linhaSelecionada seja modificado usei MutableLiveData
    https://stackoverflow.com/questions/55914752/when-to-use-mutablelivedata-and-livedata
     */


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

    fun alteraLinhaSelecionada(linha: String){
        linhaSelecionada.value = linha
    }
}