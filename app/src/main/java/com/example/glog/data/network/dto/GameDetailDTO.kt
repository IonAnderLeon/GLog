package com.example.glog.data.network.dto

import com.google.gson.annotations.SerializedName

data class GameDetailDTO(
    @SerializedName("game")
    val game: GameDTO? = null,
    @SerializedName("genreName")
    val genreName: String? = null,
    @SerializedName("platformName")
    val platformName: String? = null
)