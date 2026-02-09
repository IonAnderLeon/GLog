package com.example.glog.ui.usecase

import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.CollectionRepository
import javax.inject.Inject

class GetFavoriteGamesUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return try {
            // Llama al repositorio para obtener la colección con ID 1 (favoritos)
            val collectionResult = collectionRepository.getCollectionById(1)

            collectionResult.map { collection ->
                // Retorna los juegos de la colección
                collection.games
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}