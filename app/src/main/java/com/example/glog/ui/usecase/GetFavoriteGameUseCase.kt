package com.example.glog.ui.usecase

import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.CollectionRepository
import javax.inject.Inject

class GetFavoriteGamesUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return try {
            val collectionResult = collectionRepository.getCollectionById(1)

            collectionResult.map { collection ->
                collection.games
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}