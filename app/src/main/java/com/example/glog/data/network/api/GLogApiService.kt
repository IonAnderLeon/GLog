package com.example.glog.data.network.api

import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.CollectionGamesDTO
import com.example.glog.data.network.dto.GameDetailDTO
import com.example.glog.data.network.dto.RegisterDTO
import com.example.glog.data.network.dto.RegisterDetailDTO
import com.example.glog.data.network.dto.UserDTO
import com.example.glog.data.network.routes.K
import com.example.glog.domain.model.Game
import com.example.glog.domain.model.Genre
import com.example.glog.domain.model.Platform
import com.example.glog.domain.model.Register
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GLogApiService {

    // 🎮 Juegos
    @GET(K.GAMES)
    suspend fun getGames(
        @Query(K.GAMES_SEARCH) search: String? = null
    ): List<GameDetailDTO>

    @GET(K.GAME_BY_ID)
    suspend fun getGameById(@Path("id") id: Long): GameDetailDTO


//    @GET(K.GENRES)
//    suspend fun getGenres(): List<GenreDto>
//
//    @GET(K.PLATFORMS)
//    suspend fun getPlatforms(): List<PlatformDto>

    // 👤 Usuarios
    @GET(K.USERS)
    suspend fun getUsers(
        @Query(K.USERS_SEARCH) search: String? = null
    ): List<UserDTO>

    @GET(K.USER_BY_ID)
    suspend fun getUserById(@Path("id") id: Long): UserDTO



    // 📝 Registros
    @GET(K.REGISTERS)
    suspend fun getRegisters(
        @Query(K.REGISTERS_SEARCH) search: String? = null
    ): List<RegisterDetailDTO>

    @GET(K.REGISTERS_BY_USER)
    suspend fun getRegistersByUser(@Path("userId") userId: Long): List<RegisterDetailDTO>

    @POST(K.REGISTERS)
    suspend fun createRegister(@Body registerDto: RegisterDTO): Response<RegisterDTO>

    // 📚 Colecciones
    @GET(K.COLLECTIONS)
    suspend fun getCollections(
        @Query(K.COLLECTIONS_SEARCH) search: String? = null
    ): List<CollectionDTO>

    @POST(K.COLLECTIONS)
    suspend fun createCollection(@Body collectionDto: CollectionDTO): Response<CollectionGamesDTO>



}