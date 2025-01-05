package com.example.apptransportepublico

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencias = Preferencias(application)
    private val temaAtual = MutableLiveData(preferencias.isDarkTheme())
    val isDarkTheme: LiveData<Boolean> get() = temaAtual

    private val linhaDao = LinhaDatabase.getInstance(application).linhaDao()
    private val repository = LinhaRepository(linhaDao)

    val allLinhasAutocarro: LiveData<List<LinhaAutocarro>> = repository.allAutocarros
    val allLinhasMetro: LiveData<List<LinhaMetro>> = repository.allMetros

    private val _verParagens = MutableLiveData(true)
    val verParagens: LiveData<Boolean> get() = _verParagens
    val linhaSelecionada = MutableLiveData<String>()
    /*
    Já que eu quero que linhaSelecionada seja modificado usei MutableLiveData
    https://stackoverflow.com/questions/55914752/when-to-use-mutablelivedata-and-livedata
     */


    fun insertAutocarro(linha: LinhaAutocarro) {
        repository.insertAutocarro(linha)
    }

    fun deleteAutocarro(linha: LinhaAutocarro) {
        repository.deleteAutocarro(linha)
    }

    fun insertMetro(linha: LinhaMetro) {
        repository.insertMetro(linha)
    }

    fun deleteMetro(linha: LinhaMetro) {
        repository.deleteMetro(linha)
    }

    fun alteraLinhaSelecionada(linha: String){
        linhaSelecionada.value = linha
    }

    fun mudaTema(){
        val tema = !temaAtual.value!!
        preferencias.salvarTema(tema)
        temaAtual.value = tema
    }

    fun ligaParagens(){
        _verParagens.value = _verParagens.value != true
    }
}