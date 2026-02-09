package com.example.glog.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddGameToCollectionDTO(
    @SerialName("idGame")
    val idGame: Int
)
