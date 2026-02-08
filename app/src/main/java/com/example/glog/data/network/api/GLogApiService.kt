package com.example.glog.data.network.api

import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.CollectionGamesDTO
import com.example.glog.data.network.dto.GameDTO
import com.example.glog.data.network.dto.GameDetailDTO
import com.example.glog.data.network.dto.RegisterDTO
import com.example.glog.data.network.dto.RegisterDetailDTO
import com.example.glog.data.network.dto.UserDTO
import com.example.glog.data.network.routes.K
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GLogApiService {

    // 🎮 JUEGOS

    @GET(K.GAMES)
    suspend fun getAllGames(): List<GameDetailDTO>

    @GET(K.GAMES)
    suspend fun getGames(
        @Query(K.QUERY_SEARCH) search: String? = null
    ): List<GameDetailDTO>

    @GET(K.GAME_BY_ID)
    suspend fun getGameById(@Path("id") id: Long): GameDetailDTO

    @POST(K.GAMES)
    suspend fun createGame(@Body gameDto: GameDTO): Response<GameDTO>

    @PUT(K.GAME_BY_ID)
    suspend fun updateGame(
        @Path("id") id: Long,
        @Body gameDto: GameDTO
    ): Response<GameDTO>

    @DELETE(K.GAME_BY_ID)
    suspend fun deleteGame(@Path("id") id: Long): Response<Unit>

    // 👤 USUARIOS
    @GET(K.USERS)
    suspend fun getUsers(
        @Query(K.QUERY_SEARCH) search: String? = null
    ): List<UserDTO>

    @GET(K.USER_BY_ID)
    suspend fun getUserById(@Path("id") id: Long): UserDTO

    @POST(K.USERS)
    suspend fun createUser(@Body userDto: UserDTO): Response<UserDTO>

    @PUT(K.USER_BY_ID)
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body userDto: UserDTO
    ): Response<UserDTO>

    @DELETE(K.USER_BY_ID)
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    // 📝 REGISTROS

    @GET(K.REGISTERS)
    suspend fun getRegisters(
        @Query(K.QUERY_SEARCH) search: String? = null
    ): List<RegisterDetailDTO>

    @GET(K.REGISTERS_BY_USER)
    suspend fun getRegistersByUser(@Path("userId") userId: Long): List<RegisterDetailDTO>

    @POST(K.REGISTERS)
    suspend fun createRegister(@Body registerDto: RegisterDTO): Response<RegisterDTO>

    @PUT(K.REGISTERS_BY_ID)
    suspend fun updateRegister(
        @Path("id") id: Long,
        @Body registerDto: RegisterDTO
    ): Response<RegisterDTO>

    @DELETE(K.REGISTERS_BY_ID)
    suspend fun deleteRegister(@Path("id") id: Long): Response<Unit>

    // 📚 COLECCIONES

    @GET(K.COLLECTIONS)
    suspend fun getCollections(
        @Query(K.QUERY_SEARCH) search: String? = null
    ): List<CollectionGamesDTO>

    @GET(K.COLLECTION_BY_ID)
    suspend fun getCollectionById(@Path("id") id: Long): CollectionGamesDTO

    @POST(K.COLLECTIONS)
    suspend fun createCollection(@Body collectionDto: CollectionDTO): Response<CollectionDTO>

    @PUT(K.COLLECTION_BY_ID)
    suspend fun updateCollection(
        @Path("id") id: Long,
        @Body collectionDto: CollectionDTO
    ): Response<CollectionDTO>

    @DELETE(K.COLLECTION_BY_ID)
    suspend fun deleteCollection(@Path("id") id: Long): Response<Unit>

}