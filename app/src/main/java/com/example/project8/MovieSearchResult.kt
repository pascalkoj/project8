package com.example.project8

import com.google.gson.annotations.SerializedName

data class MovieSearchResult (
    // each aspect of the OMDB json response that we need
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: Int,
    @SerializedName("Rated") val rated: String,
    @SerializedName("Runtime") val runtime: String,
    @SerializedName("Genre") val genre: String,
    @SerializedName("Poster") val posterURL: String,
    @SerializedName("imdbRating") val imdbRating: String,
    @SerializedName("imdbID") val imdbID: String,
)