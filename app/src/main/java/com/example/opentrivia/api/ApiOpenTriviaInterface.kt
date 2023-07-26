package com.example.opentrivia.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiOpenTriviaInterface {
    @GET("/api.php")
    suspend fun getTriviaQuestion (
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type : String
    ): Response<OpenTriviaResponse>


}