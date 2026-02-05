package com.example.glog.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDTO(
    @SerialName("date")
    val date: String? = null,
    @SerialName("idGame")
    val idGame: Int? = null,
    @SerialName("idRegister")
    val idRegister: Int? = null,
    @SerialName("idUsuario")
    val idUsuario: Int? = null,
    @SerialName("playtime")
    val playtime: Double? = null
)