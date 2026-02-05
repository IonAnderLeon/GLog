package com.example.glog.data.network.routes

object K {
    const val BASE_URL = "http://localhost:8080/api"

    // Games
    const val GAMES = "/games"
    const val GAMES_SEARCH = "/games"
    const val GAME_BY_ID = "/games/{id}"
    const val GENRES = "/games/genres"
    const val PLATFORMS = "/games/platforms"

    // Users
    const val USERS = "/users"
    const val USERS_SEARCH = "/users"
    const val USER_BY_ID = "/users/{id}"

    // Registers
    const val REGISTERS = "/registers"
    const val REGISTERS_SEARCH = "/registers"
    const val REGISTERS_BY_USER = "/registers/user/{userId}"

    // Collections
    const val COLLECTIONS = "/collections"
    const val COLLECTIONS_SEARCH = "/collections"
    const val COLLECTION_BY_ID = "/collections/{id}"

    // Query params
    const val QUERY_SEARCH = "busqueda"
}