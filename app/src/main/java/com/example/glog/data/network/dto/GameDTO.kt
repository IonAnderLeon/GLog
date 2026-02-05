package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    @SerialName("genreId")
    val genreId: Int? = null,
    @SerialName("idGame")
    val id: Int? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("platformId")
    val platformId: Int? = null,
    @SerialName("rating")
    val rating: Double? = null,
    @SerialName("year")
    val year: Int? = null
)