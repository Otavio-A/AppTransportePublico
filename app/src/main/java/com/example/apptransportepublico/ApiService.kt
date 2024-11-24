package com.example.apptransportepublico

import retrofit2.http.GET

interface ApiService {
    @GET("BaZe/api/api4gj.php?nome=BUS%20stcp%20(RT)")
    suspend fun getBusData(): FeatureCollection
}