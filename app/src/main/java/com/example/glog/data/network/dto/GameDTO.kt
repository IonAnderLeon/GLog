package com.example.glog.data.network.dto

data class GameDTO(
    val id: Int,
    val image: String,
    val name: String,
    val year: Int,
    val rating: Float,
    val id_platform: Int,
    val id_genre: Int
)