package com.example.apptransportepublico


import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.ui.platform.LocalContext
// Import Classe Autocarro

// MAPA
@Composable
fun Ecra01() {
    val context = LocalContext.current

    // Configure OSMDroid with a unique user agent
    DisposableEffect(Unit) { // This is required by OSMDroid to identify your app when fetching map tiles.
        Configuration.getInstance().userAgentValue = context.packageName // Use your app's package name as the user agent
        onDispose { }
    }
    // This sets up the MapView with the desired tile source, zoom level, and center point.
    // Remember the map view instance to manage lifecycle
    val mapView = remember { MapView(context) }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach() // Clean up MapView resources
        }
    }
    // The AndroidView Composable embeds the MapView in the Compose UI hierarchy.

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(41.1579, -8.6291)) // Example: Porto, Portugal
            }
        }
    )
}



// FAVORITOS

@Composable
fun Ecra02() {
    var autocarros by remember { mutableStateOf<List<Autocarro>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            autocarros = Autocarro.filtraAuto("Linha 800") // Todo permitir usuario escolher a linha
        } catch (e: Exception) {
            errorMessage = "Erro: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(autocarros) { autocarro ->
                    AutocarroCard(autocarro)
                }
            }
        }
    }
}

@Composable
fun AutocarroCard(bus: Autocarro) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Linha: ${bus.linha}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Latitude: ${bus.latitude}", fontSize = 14.sp)
            Text(text = "Longitude: ${bus.longitude}", fontSize = 14.sp)
            Text(text = "Details: ${bus.popupContent}", fontSize = 12.sp)
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

