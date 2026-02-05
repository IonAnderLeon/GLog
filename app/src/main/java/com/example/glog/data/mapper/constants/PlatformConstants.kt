package com.example.glog.data.mapper.constants

object PlatformConstants {

    private val platformMap = mapOf(
        1 to "PC",
        2 to "PlayStation 5",
        3 to "Xbox Series X",
        4 to "Nintendo Switch",
        5 to "PlayStation 4",
        6 to "Xbox One",
        7 to "Android",
        8 to "iOS"
    )

    fun getPlatformNameById(id: Int): String {
        return platformMap[id] ?: "Plataforma desconocida"
    }
}