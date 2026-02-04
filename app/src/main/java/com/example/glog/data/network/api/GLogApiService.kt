package com.example.glog.data.network.api

import com.example.glog.domain.model.Game
import com.example.glog.domain.model.Genre
import com.example.glog.domain.model.Platform
import com.example.glog.domain.model.Register
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