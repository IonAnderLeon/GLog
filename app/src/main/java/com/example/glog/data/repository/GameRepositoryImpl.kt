package com.example.glog.data.repository

import com.example.glog.data.mapper.GameMapper
import com.example.glog.data.network.api.GameApiService
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameApiService: GameApiService,
    private val gameMapper: GameMapper
) : GameRepository {

    override suspend fun getGames(): Result<List<Game>> {
        return try {
            val response = gameApiService.getGames()
            if (response.isSuccessful) {
                val games = response.body()?.map { gameMapper.toEntity(it) } ?: emptyList()
                Result.success(games)
            } else {
                Result.failure(Exception("API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}