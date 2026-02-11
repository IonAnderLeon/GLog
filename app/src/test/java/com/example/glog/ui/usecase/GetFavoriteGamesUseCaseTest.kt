package com.example.glog.ui.usecase

import com.example.glog.domain.model.Collection
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.CollectionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFavoriteGamesUseCaseTest {

    private lateinit var collectionRepository: CollectionRepository
    private lateinit var useCase: GetFavoriteGamesUseCase

    @Before
    fun setUp() {
        collectionRepository = mockk()
        useCase = GetFavoriteGamesUseCase(collectionRepository)
    }

    @Test
    fun invoke_returnsGamesFromFavoritesCollection() = runTest {
        val games = listOf(
            Game(id = 1, title = "Zelda", imageUrl = null, releaseYear = 2023, rating = 4.5, platformName = "Switch", genreName = "Aventura"),
            Game(id = 2, title = "Elden Ring", imageUrl = null, releaseYear = 2022, rating = 4.8, platformName = "PC", genreName = "RPG")
        )
        val collection = Collection(id = 1, name = "Favoritos", description = null, games = games)
        coEvery { collectionRepository.getCollectionById(1L) } returns Result.success(collection)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Zelda", result.getOrNull()?.get(0)?.title)
        assertEquals("Elden Ring", result.getOrNull()?.get(1)?.title)
    }

    @Test
    fun invoke_emptyCollection_returnsEmptyList() = runTest {
        val collection = Collection(id = 1, name = "Favoritos", description = null, games = emptyList())
        coEvery { collectionRepository.getCollectionById(1L) } returns Result.success(collection)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun invoke_repositoryFails_returnsFailure() = runTest {
        coEvery { collectionRepository.getCollectionById(1L) } returns Result.failure(RuntimeException("Network error"))

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
