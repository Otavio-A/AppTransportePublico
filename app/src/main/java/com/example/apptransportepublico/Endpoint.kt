package com.example.apptransportepublico

import retrofit2.http.GET
//  Chamada de API para AUTOCARROS STCP
interface Endpoint {
    @GET("BaZe/api/api4gj.php?nome=BUS%20stcp%20(RT)")
    suspend fun getBusData(): FeatureCollection

    @GET("BaZe/api/api4gj.php?nome=Paragens STCP Maia (GTFS)")
    suspend fun getParagemData(): ParagemFeatureCollection

    @GET("BaZe/api/api4gj.php?nome=metro (linhas)")
    suspend fun getMetroData(): MetroFeatureCollection
}