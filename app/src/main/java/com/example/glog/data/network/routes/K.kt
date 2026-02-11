package com.example.glog.data.network.routes

object K {
    const val BASE_URL = "http://192.168.1.144:8080/api/"

    // Query param (común para todos)
    const val QUERY_SEARCH = "busqueda"

    // Games
    const val GAMES = "games"
    const val GAME_BY_ID = "games/{id}"

    // Users
    const val USERS = "users"
    const val USER_BY_ID = "users/{id}"

    // Registers
    const val REGISTERS = "registers"
    const val REGISTERS_BY_ID = "registers/{id}"
    const val REGISTERS_BY_USER = "registers/user/{userId}"

    // Collections
    const val COLLECTIONS = "collections"
    const val COLLECTION_BY_ID = "collections/{id}"
    const val COLLECTION_ADD_GAME = "collections/{id}/games"
    const val COLLECTION_REMOVE_GAME = "collections/{collectionId}/games/{gameId}"
}