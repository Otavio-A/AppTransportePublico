package com.example.apptransportepublico


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

import androidx.compose.material3.SearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Import Classe Autocarro

// MAPA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra01(viewModel: MainViewModel) {
    val context = LocalContext.current

    val linhaSelecionada by viewModel.linhaSelecionada.observeAsState("Linha 800")
    val listaFavoritos by viewModel.allLinhas.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf(linhaSelecionada) }
    var active by remember { mutableStateOf(false)}
    var currentLinha by remember { mutableStateOf(linhaSelecionada) }
    var autocarros by remember { mutableStateOf<List<Autocarro>>(emptyList()) }
    val mapView = remember { MapView(context) }
    var sugestoes = remember { mutableStateListOf("Linha 800", "Linha 200" ) }
    val foiFavoritado = listaFavoritos.any { it.linha == searchQuery }  // Verifica se a linha já foi favoritada
    val autocarroFavoritado = LinhaAutocarro(searchQuery)
    LaunchedEffect(currentLinha) {  // Como filtraauto é uma suspend function eu preciso desse Launched Effect
        autocarros = Autocarro.filtraAuto(currentLinha)
    }

    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { mapView.onDetach()}
    }

    Column (modifier = Modifier.fillMaxSize()){
        /*
        SEARCHBAR RETIRADA DE https://composables.com/material3/searchbar, USANDO O TUTORIAL https://youtu.be/90gokceSYdM?si=n9A4o5F0YP3MN4L-
         */

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = searchQuery,
            onQueryChange = {searchQuery = it},
            onSearch = {
                if (searchQuery.isNotEmpty()){
                    currentLinha = searchQuery
                    viewModel.alteraLinhaSelecionada(searchQuery)
                }
                if (!sugestoes.contains(searchQuery)) {     // Para não repitir historico
                    sugestoes.add(searchQuery)
                }
                active = false
            },
            active = active,
            onActiveChange = {active = it},
            placeholder = { Text(text = stringResource(id = R.string.PesquisarLinha))},
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.baseline_search_24),
                    contentDescription = "Search Icon",
                )
            },
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (searchQuery.isNotEmpty())
                                searchQuery = ""        //Caso tenha algo escrito vai apagar
                            else
                                active = false          // Caso não tenha, a janela fecha : ^)
                        },
                        painter = painterResource(R.drawable.baseline_close_24),
                        contentDescription = "Close Icon",
                    )
                }
                if (!active && searchQuery.isNotEmpty())
                {
                    if (foiFavoritado){
                        Icon(
                            modifier = Modifier.clickable{
                                viewModel.deleteLinha(autocarroFavoritado)
                            },
                            painter = painterResource(R.drawable.baseline_star_24),
                            contentDescription = "Favorito Icon"
                        )
                    }
                    else {
                        Icon(
                            modifier = Modifier.clickable {
                                viewModel.insertLinha(autocarroFavoritado)
                            },
                            painter = painterResource(R.drawable.baseline_star_outline_24),
                            contentDescription = "Não favorito Icon"
                        )
                    }
                }
            }
        )
        {
            sugestoes.forEach{ sugestao ->
                Row(modifier = Modifier
                    .clickable {
                        currentLinha = sugestao
                        searchQuery = sugestao
                        active = false
                    }
                    .padding(all = 14.dp)){
                    Icon(
                        modifier = Modifier.padding(end = 14.dp),
                        painter = painterResource(R.drawable.baseline_history_24),
                        contentDescription = "History Icon"
                    )
                    Text(text = sugestao)
                }
            }
        }
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.3) // Quanto maior, mais zoom
                    controller.setCenter(GeoPoint(41.2357400, -8.6199000)) // Centro da Maia

                    overlays.clear()
                    overlays.add(CopyrightOverlay(context))

                    autocarros.forEach{ auto -> val marker = Marker(this)
                        marker.position = GeoPoint(auto.latitude, auto.longitude)
                        marker.title = auto.popupContent
                        overlays.add(marker)
                    }
                }
            }
        )
    }
}

// FAVORITOS

@Composable
fun Ecra02(viewModel: MainViewModel, navController: NavController) {
    val linhas by viewModel.allLinhas.observeAsState(emptyList())
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ){
        Text(text = stringResource(id = R.string.Favoritos),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.padding(5.dp)
        ){
            items(linhas){
                linha -> LinhaCard(
                linha,selecionar = {
                    viewModel.alteraLinhaSelecionada(linha.linha)
                    navController.navigate(Destino.Ecra01.route)    //Direciona o usuario para o mapa
                                   },
                remover = {viewModel.deleteLinha(linha)})
            }
        }
    }
}

@Composable
fun LinhaCard(linha : LinhaAutocarro,selecionar : () -> Unit, remover: () -> Unit){
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                selecionar()
            }
        ,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = linha.linha,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                modifier = Modifier.clickable { remover() },
                painter = painterResource(R.drawable.baseline_close_24),
                contentDescription = "Remover Icon"
            )
        }
    }
}

// SETTINGS
@Composable
fun Ecra03() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra03),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

