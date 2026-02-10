package com.example.glog.data.mapper.constants

object PlatformConstants {

    private val platformMap = mapOf(
        1 to "PC",
        2 to "PS5",
        3 to "XboxX",
        4 to "Switch",
        5 to "PS4",
        6 to "XboxOne",
        7 to "Android",
        8 to "iOS",
        17 to "Wii"
    )

    fun getPlatformNameById(id: Int): String {
        return platformMap[id] ?: "Plataforma desconocida"
    }
}