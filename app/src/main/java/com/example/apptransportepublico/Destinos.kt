package com.example.apptransportepublico

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.baseline_add_location_alt_24, title = "Map")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.sharp_assignment_24, title = "Favorites")
    object Ecra03 : Destino(route = "ecra03", icon = R.drawable.baseline_settings, title = "Settings")
    companion object {
        val toList = listOf(Ecra01, Ecra02, Ecra03)
    }
}