package com.example.glog.domain.model

import java.util.Date

//Preguntar si sería posible el date

data class Register(
    val id: Int,
    val date: String?,  // O LocalDate si prefieres
    val playtime: Double?,
    val gameId: Int?,
    val gameName: String?,  // Del DetailDTO
    val userId: Int?,
    val userName: String?   // Del DetailDTO
)
