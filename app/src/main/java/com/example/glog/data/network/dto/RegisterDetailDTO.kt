package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDetailDTO(
    @SerialName("gameName")
    val gameName: String? = null,
    @SerialName("gameImageUrl")
    val gameImageUrl: String? = null,
    @SerialName("register")
    val register: RegisterDTO? = null,
    @SerialName("userName")
    val userName: String? = null
)