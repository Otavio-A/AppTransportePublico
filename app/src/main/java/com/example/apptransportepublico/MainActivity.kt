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


// OSM

//Database
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState

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