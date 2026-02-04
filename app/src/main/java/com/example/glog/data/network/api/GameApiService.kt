package com.example.glog.data.network.api

import okhttp3.Response
import retrofit2.http.GET

interface GameApiService {

    @GET("games")
    suspend fun getGames(): Response<List<GameDto>>

    @GET("games/{id}")
    suspend fun getGame(@Path("id") id: Long): Response<GameDto>

    @POST("games")
    suspend fun createGame(@Body gameDto: GameDto): Response<GameDto>

    @PUT("games/{id}")
    suspend fun updateGame(
        @Path("id") id: Long,
        @Body gameDto: GameDto
    ): Response<GameDto>

    @DELETE("games/{id}")
    suspend fun deleteGame(@Path("id") id: Long): Response<Unit>

    // Para operaciones de usuario/games (colección)
    @GET("users/{userId}/games")
    suspend fun getUserGames(@Path("userId") userId: Long): Response<List<GameDto>>

    @POST("users/{userId}/games/{gameId}")
    suspend fun addGameToCollection(
        @Path("userId") userId: Long,
        @Path("gameId") gameId: Long
    ): Response<Unit>

    // Ejemplo con parámetros de búsqueda
    @GET("games/search")
    suspend fun searchGames(
        @Query("title") title: String?,
        @Query("platform") platformId: Long?,
        @Query("genre") genreId: Long?
    ): Response<List<GameDto>>
}