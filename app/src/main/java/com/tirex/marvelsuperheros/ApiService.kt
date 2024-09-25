package com.tirex.marvelsuperheros

import com.example.marvelapi.SuperHeroDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v1/public/characters")
    suspend fun getCharacters(
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
    ): Response<SuperHeroDataResponse>

    @GET("v1/public/characters")
    suspend fun searchCharacters(
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
        @Query("nameStartsWith") nameStartsWith: String? = null,
    ): Response<SuperHeroDataResponse>

    @GET("/v1/public/characters/{characterId}")
    suspend fun getSuperHeroDetails(
        @Path("characterId") superheroId:Int,
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String
        ): Response<SuperHeroDetailsResponse>
}
