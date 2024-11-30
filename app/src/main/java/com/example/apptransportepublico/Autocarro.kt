package com.example.apptransportepublico

data class FeatureCollection(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val icon: String,
    val `marker-color`: String,
    val popupContent: String
)

data class Geometry(
    val coordinates: List<Double>,
    val type: String
)

class Autocarro {
    suspend fun filtraAuto(linha: String): List<Map<String, Any>> {
        val autocarrosFiltrados = mutableListOf<Map<String, Any>>()

        // JSON da API
        val featureCollection = RetrofitInstance.apiService.getBusData()

        // Filtrar atributos de cada item, no caso de cada linha
        featureCollection.features.forEach { feature ->
            if (feature.properties.popupContent.contains("<h1>$linha</h1>")) {
                val latitude = feature.geometry.coordinates[1]
                val longitude = feature.geometry.coordinates[0]

                autocarrosFiltrados.add(
                    mapOf(
                        "linha" to linha,
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "popupContent" to feature.properties.popupContent
                    )
                )
            }
        }

        return autocarrosFiltrados
    }
}