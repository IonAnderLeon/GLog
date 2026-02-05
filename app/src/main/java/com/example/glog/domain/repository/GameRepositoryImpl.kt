package com.example.glog.domain.repository

import com.example.glog.data.mapper.GameMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.domain.model.Game
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val apiService: GLogApiService,
    private val gameMapper: GameMapper
) : GameRepository {

    override suspend fun getGames(search: String?): Result<List<Game>> {
        return try {
            val dtos = apiService.getGames(search)
            val games = dtos.map { gameMapper.toEntity(it) }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameById(id: Long): Result<Game> {
        return try {
            val dto = apiService.getGameById(id)
            val game = gameMapper.toEntity(dto)
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

