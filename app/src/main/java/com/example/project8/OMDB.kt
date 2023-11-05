package com.example.project8

import android.graphics.Movie
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val API_KEY = "a3b294d6"

interface OMDbApi {
    // To get a movie based on search
    @GET("?apikey=$API_KEY")
    fun getMovie(@Query("t") title: String): Call<MovieSearchResult>
}