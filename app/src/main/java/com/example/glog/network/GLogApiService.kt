package com.example.glog.network

import com.example.glog.data.model.Game
import com.example.glog.data.model.Genre
import com.example.glog.data.model.Platform
import com.example.glog.data.model.Register
import retrofit2.http.GET

interface GLogApiService {

    @GET("games")
    suspend fun getGames(): List<Game>

    @GET("genres")
    suspend fun getGenre(): List<Genre>

    @GET("platforms")
    suspend fun getPlatform(): List<Platform>

    @GET("register")
    suspend fun getRegister(): List<Register>



}