package com.example.glog.domain.repository

import com.example.glog.domain.model.Game

interface GameRepository {
    suspend fun getGames(search: String? = null): Result<List<Game>>
    suspend fun getGameById(id: Long): Result<Game>
}