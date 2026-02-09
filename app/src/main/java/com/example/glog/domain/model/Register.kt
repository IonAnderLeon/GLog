package com.example.glog.domain.model

data class Register(
    val id: Int,
    val date: String?,
    val playtime: Double?,
    val gameId: Int?,
    val gameName: String?,
    val gameImageUrl: String? = null,
    val userId: Int?,
    val userName: String?
)
