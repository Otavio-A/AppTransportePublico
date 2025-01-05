package com.example.apptransportepublico


import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

import androidx.compose.material3.SearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

import android.content.Context
import android.content.pm.PackageManager

// Para pegar a localização
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.delay
import org.osmdroid.views.overlay.Polyline


// Import Classe Autocarro

// MAPA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra01(viewModel: MainViewModel) {
    val context = LocalContext.current

    val linhaSelecionada by viewModel.linhaSelecionada.observeAsState("Linha 800")
    val listaAutocarros by viewModel.allLinhasAutocarro.observeAsState(emptyList())
    val listaMetros by viewModel.allLinhasMetro.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf(linhaSelecionada) }
    var active by remember { mutableStateOf(false)}
    var currentLinha by remember { mutableStateOf(linhaSelecionada) }
    var autocarros by remember { mutableStateOf<List<Autocarro>>(emptyList()) }
    var paragens by remember { mutableStateOf<List<Paragem>>(emptyList()) }
    val mapView = remember { MapView(context) }
    var sugestoes = remember { mutableStateListOf("Linha 800", "Linha 200", "Linha B" ,"Linha C", "Linha E") }
    val foiFavoritado = listaAutocarros.any { it.linha == searchQuery } || listaMetros.any { it.linha == searchQuery }
    val autocarroFavoritado = LinhaAutocarro(searchQuery)
    var temPermissao by remember { mutableStateOf(false) }
    var localizacaoUsuario by remember { mutableStateOf<GeoPoint?>(null) }  //Já que eu não sei se tenho ou não a permissãoo
    var linhasMetro by remember { mutableStateOf<List<Metro>>(emptyList()) }
    val verParagens by viewModel.verParagens.observeAsState(true)
    PermissaoLocalizacao(
        onPermissionGranted = { temPermissao = true },
        onPermissionDenied = { temPermissao = false }
    )

    LaunchedEffect(currentLinha, temPermissao) {  // Como filtraauto é uma suspend function eu preciso desse Launched Effect
        autocarros = Autocarro.filtraAuto(currentLinha)
        linhasMetro = Metro.filtraMetro(currentLinha)
        Paragem.pegaParagens().let { paragemLista ->
            paragens = paragemLista
            Log.d("Paragens", "Fetched paragens: $paragens")
        }
        if (temPermissao){
            pegaLocalizacao(
                context = context,
                onSuccess = { location -> localizacaoUsuario = GeoPoint(location.latitude, location.longitude)},
                onError = {exception -> println( "Error: ${exception.message}")}
            )
            while (true) {      // Aqui eu to atualizando a localização do usuario em tempo real
                delay(10000)    // Em milisegundos, ta pra atualizar a cada 10 seg : ^)
                pegaLocalizacao(
                    context = context,
                    onSuccess = { location ->
                        localizacaoUsuario = GeoPoint(location.latitude, location.longitude)
                    },
                    onError = { exception ->
                        println("Error: ${exception.message}")
                    }
                )
            }
        }

    }

    DisposableEffect(Unit) {
        org.osmdroid.config.Configuration.getInstance().userAgentValue = context.packageName
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
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                inputFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            ),
            active = active,
            onActiveChange = {active = it},
            placeholder = { Text(
                text = stringResource(id = R.string.PesquisarLinha)) },
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.baseline_search_24),
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                                viewModel.deleteAutocarro(autocarroFavoritado)
                            },
                            painter = painterResource(R.drawable.baseline_star_24),
                            contentDescription = "Favorito Icon"
                        )
                    }
                    else {
                        Icon(
                            modifier = Modifier.clickable {
                                viewModel.insertAutocarro(autocarroFavoritado)
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


                    overlays.clear()
                    overlays.add(CopyrightOverlay(context))

                    // A ordem em que markers são adicionados define a prioridade. Aqui o Autocarro fica em cima, depois o usuario e por ultimo as paragens
                    // TODO Fazer a linha de metro so aparecer se pesquisar
                    linhasMetro.forEach { metroLine ->
                        val polyline = Polyline().apply {
                            setPoints(metroLine.coordinates)
                            color = ContextCompat.getColor(context, R.color.Lime)
                            width = if (metroLine.underConstruction) 4f else 8f
                            title = metroLine.linha
                        }
                        overlays.add(polyline)
                    }
                   if (verParagens){
                       paragens.forEach { paragem ->
                           val marker = Marker(this)
                           marker.position = GeoPoint(paragem.latitude, paragem.longitude)
                           marker.title = paragem.linha // Por algum motivo não mostra?
                           marker.icon = ContextCompat.getDrawable(context, R.drawable.bus_stop)
                           marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                           overlays.add(marker)
                       }
                   }
                    if (localizacaoUsuario != null){
                        localizacaoUsuario?.let {
                            controller.setCenter(it)
                            val marker = Marker(this)
                            marker.position = it
                            marker.icon = ContextCompat.getDrawable(context, R.drawable.baseline_person_pin_circle_24)
                            marker.title = context.getString(R.string.You)
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            overlays.add(marker)
                        }
                    }
                    else{
                       controller.setCenter(GeoPoint(41.2357, -8.6199))
                    }



                    autocarros.forEach{ auto -> val marker = Marker(this)
                        marker.position = GeoPoint(auto.latitude, auto.longitude)
                        marker.title = "${auto.linha} \n${auto.velocidade}"
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        overlays.add(marker)
                    }


                }
            }
        )
    }
}

fun pegaLocalizacao(context: Context, onSuccess: (Location) -> Unit, onError: (Exception) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    try {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError(SecurityException("Permissão de localização não obtida"))
            return
        }
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000 // Update em 5000 milisegundos
        ).build()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                onSuccess(location)
            } else {
                onError(Exception("Location is null"))
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    } catch (e: Exception) {
        onError(e)
    }
}

// FAVORITOS

@Composable
fun Ecra02(viewModel: MainViewModel, navController: NavController) {
    val autocarros by viewModel.allLinhasAutocarro.observeAsState(emptyList())
    val metros by viewModel.allLinhasMetro.observeAsState(emptyList())

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
        ) {
            items(autocarros) { autocarro ->
                TransporteCard(
                    name = autocarro.linha,
                    type = "Autocarro",
                    selecionar = {
                        viewModel.alteraLinhaSelecionada(autocarro.linha)
                        navController.navigate(Destino.Ecra01.route)
                    },
                    remover = { viewModel.deleteAutocarro(autocarro) }
                )
            }
            items(metros) { metro ->
                TransporteCard(
                    name = metro.linha,
                    type = "Metro",
                    selecionar = {
                        viewModel.alteraLinhaSelecionada(metro.linha)
                        navController.navigate(Destino.Ecra01.route)
                    },
                    remover = { viewModel.deleteMetro(metro) }
                )
            }
        }
    }
}

@Composable
fun TransporteCard(
    name: String,
    type: String,
    selecionar: () -> Unit,
    remover: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { selecionar() },
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 18.sp
                )
                Text(
                    text = type,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
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
fun Ecra03(viewModel: MainViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.observeAsState(false)
    val verParagens by viewModel.verParagens.observeAsState(true)

    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        if (isDarkTheme){
            Text(text = "Dark Mode")
        }
        else
        {
            Text(text = "Light Mode")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { viewModel.mudaTema() },
            colors = SwitchDefaults.colors(
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.AlterarVisibilidadeParagem)
        )
        Switch(
            checked = verParagens,
            onCheckedChange = { viewModel.ligaParagens() },
            colors = SwitchDefaults.colors(
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}


