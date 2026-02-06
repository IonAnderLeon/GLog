package com.example.glog.data.network.dto

import com.google.gson.annotations.SerializedName

data class GameDTO(
    @SerializedName("genreId")
    val genreId: Int? = null,
    @SerializedName("idGame")
    val id: Int? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("platformId")
    val platformId: Int? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("year")
    val year: Int? = null
)