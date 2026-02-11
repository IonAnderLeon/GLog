package com.example.glog.ui.viewmodels

import com.example.glog.MainCoroutineRule
import com.example.glog.domain.model.Collection
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.CollectionRepository
import com.example.glog.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameInfoViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val gameRepository: GameRepository = mockk()
    private val collectionRepository: CollectionRepository = mockk()
    private lateinit var viewModel: GameInfoViewModel

    @Test
    fun loadGame_success_setsGameAndSimilarGames() = runTest {
        val game = Game(1, "Zelda", null, 2023, 4.5, "Switch", "Aventura", "Desc")
        val allGames = listOf(
            game,
            Game(2, "Other Aventura", null, 2022, 4.0, "Switch", "Aventura", null)
        )
        coEvery { gameRepository.getGameById(1L) } returns Result.success(game)
        coEvery { gameRepository.getAllGames() } returns Result.success(allGames)
        coEvery { collectionRepository.getCollectionById(1L) } returns Result.success(
            Collection(1, "Fav", null, games = emptyList())
        )
        viewModel = GameInfoViewModel(gameRepository, collectionRepository)

        viewModel.loadGame(1)

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(game, state.game)
        assertEquals(1, state.similarGames.size)
        assertEquals("Other Aventura", state.similarGames.first().title)
    }

    @Test
    fun loadGame_gameInFavorites_setsIsInFavoritesTrue() = runTest {
        val game = Game(1, "Zelda", null, 2023, 4.5, "Switch", "Aventura", null)
        val favCollection = Collection(1, "Favoritos", null, games = listOf(game))
        coEvery { gameRepository.getGameById(1L) } returns Result.success(game)
        coEvery { gameRepository.getAllGames() } returns Result.success(emptyList())
        coEvery { collectionRepository.getCollectionById(1L) } returns Result.success(favCollection)
        viewModel = GameInfoViewModel(gameRepository, collectionRepository)

        viewModel.loadGame(1)

        assertTrue(viewModel.uiState.first().isInFavorites)
    }

    @Test
    fun loadGame_failure_setsError() = runTest {
        coEvery { gameRepository.getGameById(1L) } returns Result.failure(RuntimeException("Not found"))
        viewModel = GameInfoViewModel(gameRepository, collectionRepository)

        viewModel.loadGame(1)

        assertEquals("Not found", viewModel.uiState.first().error)
    }

    @Test
    fun clearToastMessage_clearsMessageForToast() = runTest {
        viewModel = GameInfoViewModel(gameRepository, collectionRepository)

        viewModel.clearToastMessage()

        assertNull(viewModel.uiState.first().messageForToast)
    }
}
