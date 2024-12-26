package com.example.apptransportepublico

/*
DATA CLASSES COM DADOS DO JSON
====================================================================================================
*/
data class FeatureCollection( // Root json
    val type: String,
    val features: List<Feature>
)

data class Feature(  // Cada item vai ser um autocarro
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
    val coordinates: List<Double>, // [longitude, latitude]
    val type: String
)

/*
CLASSE AUTOCARRO
====================================================================================================
 */

class Autocarro(
    val linha: String,
    val latitude: Double,
    val longitude: Double,
    val popupContent: String? // Teoricamente nunca deveria vir null : ^)
) {
    companion object {
        suspend fun filtraAuto(linha: String): List<Autocarro> {
            return try{
            val autocarros = mutableListOf<Autocarro>()

            // JSON da API
            val featureCollection = RetrofitInstance.apiService.getBusData()

            featureCollection.features.forEach { feature ->
                val popupContent = feature.properties.popupContent
                if (popupContent?.contains("<h1>$linha</h1>") == true) {
                    val latitude = feature.geometry.coordinates[1]
                    val longitude = feature.geometry.coordinates[0]

                    autocarros.add(
                        Autocarro(
                            linha = linha,
                            latitude = latitude,
                            longitude = longitude,
                            popupContent = popupContent
                        )
                    )
                }
            }
            return autocarros
            }
            catch (e: Exception){
                e.printStackTrace()
                emptyList()
            }
        }
    }
}