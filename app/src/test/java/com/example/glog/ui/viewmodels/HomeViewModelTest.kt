package com.example.glog.ui.viewmodels

import com.example.glog.MainCoroutineRule
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val gameRepository: GameRepository = mockk()
    private lateinit var viewModel: HomeViewModel

    @Test
    fun loadGames_success_populatesSectionsAndClearsLoading() = runTest {
        val games = listOf(
            Game(1, "Game1", null, 2023, 4.0, "PC", "Aventura", null),
            Game(2, "Game2", null, 2022, 4.5, "PC", "RPG", null),
            Game(3, "Game3", null, 2024, 3.5, "Switch", "Aventura", null)
        )
        coEvery { gameRepository.getAllGames() } returns Result.success(games)
        viewModel = HomeViewModel(gameRepository)

        viewModel.loadGames()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(3, state.recentGames.size)
        assertEquals(3, state.popularGames.size)
        assertEquals(2, state.pcGames.size)
        assertEquals(2, state.adventureGames.size)
    }

    @Test
    fun loadGames_success_recentGamesLimitedTo10() = runTest {
        val games = (1..15).map { i ->
            Game(i, "Game$i", null, 2020 + i, 4.0, "PC", "RPG", null)
        }
        coEvery { gameRepository.getAllGames() } returns Result.success(games)
        viewModel = HomeViewModel(gameRepository)

        viewModel.loadGames()

        val state = viewModel.uiState.first()
        assertEquals(10, state.recentGames.size)
    }

    @Test
    fun loadGames_failure_setsErrorAndClearsLoading() = runTest {
        coEvery { gameRepository.getAllGames() } returns Result.failure(RuntimeException("API error"))
        viewModel = HomeViewModel(gameRepository)

        viewModel.loadGames()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals("API error", state.error)
    }

    @Test
    fun onSearchTextChange_updatesSearchText() = runTest {
        viewModel = HomeViewModel(gameRepository)

        viewModel.onSearchTextChange("zelda")

        assertEquals("zelda", viewModel.uiState.first().searchText)
    }

    @Test
    fun onToggleSearch_togglesShowSearchBar() = runTest {
        viewModel = HomeViewModel(gameRepository)
        val initial = viewModel.uiState.first().showSearchBar

        viewModel.onToggleSearch()

        assertEquals(!initial, viewModel.uiState.first().showSearchBar)

        viewModel.onToggleSearch()

        assertEquals(initial, viewModel.uiState.first().showSearchBar)
    }
}
