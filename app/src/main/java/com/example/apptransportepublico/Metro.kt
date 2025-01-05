package com.example.apptransportepublico

import org.osmdroid.util.GeoPoint

data class MetroFeatureCollection(
    val type: String,
    val features: List<MetroFeature>
)

data class MetroFeature(
    val type: String,
    val properties: MetroProperties,
    val geometry: MetroGeometry
)

data class MetroProperties(
    val popupContent: String,
    val total_distance: String,
    val unit: String,
    val stroke: String,
    val underConstruction: Boolean
)

data class MetroGeometry(
    val coordinates: List<List<Double>>, // List of coordinate pairs [longitude, latitude]
    val type: String
)

data class Metro(
    val linha: String,
    val totalDistance: Double,
    val unit: String,
    val strokeColor: String,
    val underConstruction: Boolean,
    val coordinates: List<GeoPoint>
) {
    companion object {
        suspend fun filtraMetro(linha: String): List<Metro> {
            return try {
                val linhasMetro = mutableListOf<Metro>()

                // Fetch JSON from API
                val featureCollection = RetrofitInstance.apiService.getMetroData()

                featureCollection.features.forEach { feature ->
                    val properties = feature.properties
                    val geometry = feature.geometry
                    val name = properties.popupContent
                        .substringAfter("-> ") // px;'>
                        .substringBefore("< ->") // Extract metro line name
                    val totalDistance = properties.total_distance.toDoubleOrNull() ?: 0.0
                    val unit = properties.unit
                    val strokeColor = properties.stroke
                    val underConstruction = properties.underConstruction

                    if (name?.contains("$linha") == true){
                        val coordinates = geometry.coordinates.map { coordinatePair ->
                            GeoPoint(coordinatePair[1], coordinatePair[0]) // Convert to GeoPoint
                        }
                        linhasMetro.add(
                            Metro(
                                linha = name,
                                totalDistance = totalDistance,
                                unit = unit,
                                strokeColor = strokeColor,
                                underConstruction = underConstruction,
                                coordinates = coordinates
                            )
                        )
                    }
                }

                linhasMetro
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
