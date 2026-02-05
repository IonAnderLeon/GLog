package com.example.glog.data.mapper.constants

object GenreConstants {

    private val genreMap = mapOf(
        1 to "Acción",
        2 to "Aventura",
        3 to "RPG",
        4 to "Estrategia",
        5 to "Deportes",
        6 to "Carreras",
        7 to "Shooter",
        8 to "Indie",
        9 to "Simulación",
        10 to "Puzzle",
        11 to "Skibidi"
    )

    fun getGenreNameById(id: Int): String {
        return genreMap[id] ?: "Género desconocido"
    }
}