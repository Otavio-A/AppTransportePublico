package com.example.apptransportepublico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.apptransportepublico.ui.theme.AppTransportePublicoTheme

import androidx.compose.foundation.layout.Box
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


// OSM

//Database
import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.contracts.contract

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = LinhaAutocarroViewModelFactory(application)
        val passaViewModelFactory: MainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        //enableEdgeToEdge()
        setContent {
            val isDarkTheme by passaViewModelFactory.isDarkTheme.observeAsState(false)
            AppTransportePublicoTheme (darkTheme = isDarkTheme){
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProgramaPrincipal(passaViewModelFactory)
                }
            }
        }
    }
}

@Composable
fun ProgramaPrincipal(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, appItems = Destino.toList) },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNavigation(navController = navController, viewModel)
            }
        }
    )
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController, startDestination = Destino.Ecra01.route) {
        composable(Destino.Ecra01.route) {
            Ecra01(viewModel)
        }
        composable(Destino.Ecra02.route) {
            Ecra02(viewModel, navController)
        }
        composable(Destino.Ecra03.route) {
            Ecra03(viewModel)
        }
    }
}

class LinhaAutocarroViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
}



@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title, tint=if(currentRoute == item.route) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(.4F)) },
                label = { Text(text = item.title, color = if(currentRoute == item.route) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(.4F)) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer, // esta instrução devia funcionar para o efeito (animação), para o ícone e para a cor do texto, mas só funciona para o efeito
                unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.4f), // esta instrução não funciona, por isso resolve-se acima no 'tint' do icon e na 'color' da label
                alwaysShowLabel = true, // colocar 'false' significa que o texto só aparece debaixo do ícone selecionado (em vez de debaixo de todos)
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route -> popUpTo(route) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

 /*
If you declare any dangerous permissions, and if your app is installed on a device that runs Android 6.0 (API level 23) or higher, you must request the dangerous permissions at runtime
https://developer.android.com/training/permissions/requesting
 */
@Composable
fun PermissaoLocalizacao(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
){
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false)}
    val permissaoObtida = remember(context){
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    val launcherPermissaoLocalizacao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                showRationale = true
                onPermissionDenied()
            }
        }
    )

     LaunchedEffect(key1 = Unit) {
         if (!permissaoObtida){
             launcherPermissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
         }
         else{
             onPermissionGranted()
         }
     }
     if (showRationale){
         Column (modifier = Modifier.fillMaxWidth()){
             Text(text = stringResource(id = R.string.PedirPermissao))

             }
         Spacer(modifier = Modifier.height(16.dp))
         Button(
             onClick = {
                 launcherPermissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                 showRationale = false
             }
         ){
             Text(text = stringResource(id = R.string.PedirPermissaoNovamente))
         }
     }
}
