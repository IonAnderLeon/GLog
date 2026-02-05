package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    @SerialName("idUser")
    val idUser: Int? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("nickname")
    val nickname: String? = null
)