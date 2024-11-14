package com.example.apptransportepublico


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


// MAPA
@Composable
fun Ecra01() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        var isMapLoaded by remember { mutableStateOf(false) }
                // Add GoogleMap here
                GoogleMap(
                     modifier = Modifier.fillMaxSize(),
                     onMapLoaded = {isMapLoaded = true}
                )
        if (!isMapLoaded) {
            AnimatedVisibility(
                visible = !isMapLoaded,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )
            }
        }
    }
}





// FAVORITOS
@Composable
fun Ecra02() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra02),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
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