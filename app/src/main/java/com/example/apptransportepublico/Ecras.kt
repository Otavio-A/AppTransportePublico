package com.example.apptransportepublico


import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

// Import Classe Autocarro

// MAPA
@Composable
fun Ecra01() {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var currentLinha by remember { mutableStateOf("Linha 800") }
    var buses by remember { mutableStateOf<List<Autocarro>>(emptyList()) }

    LaunchedEffect(currentLinha) {  // Como filtraauto é uma suspend function eu preciso desse Launched Effect
        buses = Autocarro.filtraAuto(currentLinha)
    }
    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }
    val mapView = remember { MapView(context) }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    Column (modifier = Modifier.fillMaxSize()){
        SearchBar(
            query = searchQuery,
            onQueryChange = {searchQuery = it},
            onSearch = {linha -> currentLinha = linha}
        )


        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(41.1579, -8.6291)) // Example: Porto, Portugal PLACEHOLDER todo mudar para maia

                    overlays.clear()
                    overlays.add(CopyrightOverlay(context))

                    buses.forEach{bus -> val marker = Marker(this)
                        marker.position = GeoPoint(bus.latitude, bus.longitude)
                        marker.title = bus.popupContent
                        overlays.add(marker)
                    }
                }
            }
        )
    }
}




@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(8.dp),
        placeholder = { Text(text = stringResource(id = R.string.PesquisarLinha))},
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { onSearch(query)}) {
                Icon(imageVector = Icons.Default.Search, contentDescription = stringResource(R.string.imagem_pesquisa))
            }
        }
    )
}
// FAVORITOS

@Composable
fun Ecra02(viewModel: MainViewModel) {
    val linhas by viewModel.allLinhas.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Linhas de Autocarro",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(linhas) { linha ->
                LinhaAutocarroCard(linha)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { viewModel.insertLinha(LinhaAutocarro("Linha 800")) }) {
                Text(text = "Add Linha 800")
            }
            Button(onClick = { viewModel.deleteLinha(LinhaAutocarro("Linha 800")) }) {
                Text(text = "Remove Linha 800")
            }
        }
    }
}

@Composable
fun LinhaAutocarroCard(linha: LinhaAutocarro) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Linha: ${linha.linha}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

