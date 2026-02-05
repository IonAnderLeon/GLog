package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDetailDTO(
    @SerialName("game")
    val game: GameDTO? = null,
    @SerialName("genreName")
    val genreName: String? = null,
    @SerialName("platformName")
    val platformName: String? = null
)