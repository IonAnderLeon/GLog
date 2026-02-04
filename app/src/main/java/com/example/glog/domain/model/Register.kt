package com.example.glog.domain.model

import java.util.Date

//Preguntar si sería posible el date

data class Register(
    val id_register: Int,
    val date: String,
    val playtime: Float,
    val id_game: Int
)
