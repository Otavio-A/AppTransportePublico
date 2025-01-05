package com.example.apptransportepublico

data class ParagemFeatureCollection(
    val type: String,
    val features: List<ParagemFeature>
)

data class ParagemFeature(
    val type: String,
    val properties: ParagemProperties,
    val geometry: ParagemGeometry
)

data class ParagemProperties(
    val icon: String,
    val popupContent: String
)

data class ParagemGeometry(
    val coordinates: List<Double>, // [longitude, latitude]
    val type: String
)

data class Paragem(
    val linha: String,
    val latitude: Double,
    val longitude: Double,
    val popupContent: String?
){
    companion object {
        suspend fun pegaParagens(): List<Paragem> {
            return try {
                val paragens = mutableListOf<Paragem>()

                // JSON da API
                val featureCollection = RetrofitInstance.apiService.getParagemData()

                featureCollection.features.forEach { feature ->
                    val popupContent = feature.properties.popupContent
                    val linha = linhaPopup(popupContent)

                    // Teoricamente nunca vem null mas vai que
                    if (!linha.isNullOrEmpty()) {
                        val latitude = feature.geometry.coordinates[1]
                        val longitude = feature.geometry.coordinates[0]

                        paragens.add(
                            Paragem(
                                linha = "Paragem: $linha",
                                latitude = latitude,
                                longitude = longitude,
                                popupContent = popupContent
                            )
                        )
                    }
                }
                paragens
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
        private fun linhaPopup(popupContent: String?): String? {
            return popupContent
                ?.substringAfter("Nome:")
                ?.substringBefore("<br>")  // O nome da linha esta entre o header
        }
    }
}