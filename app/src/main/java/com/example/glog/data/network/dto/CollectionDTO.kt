package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDTO(
    @SerialName("description")
    val description: String? = null,
    @SerialName("idCollection")
    val idCollection: Int? = null,
    @SerialName("name")
    val name: String? = null
)