package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionGamesDTO(
    @SerialName("collection")
    val collection: CollectionDTO? = null,
    @SerialName("games")
    val games: List<GameDTO?>? = null
)