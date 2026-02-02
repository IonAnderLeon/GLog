package com.example.glog.data.model

data class Game(
    val id_game: Int,
    val image: String,
    val name: String,
    val year: Int,
    val rating: Float,
    val id_platform: Int,
    val id_genre: Int
)
