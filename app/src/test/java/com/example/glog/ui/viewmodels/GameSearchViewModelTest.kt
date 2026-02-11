package com.example.glog.ui.viewmodels

import com.example.glog.MainCoroutineRule
import com.example.glog.domain.repository.GameRepository
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class GameSearchViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val gameRepository: GameRepository = mockk()
    private lateinit var viewModel: GameSearchViewModel

    @Test
    fun searchGames_blankQuery_clearsResultsAndStopsSearching() = runTest {
        viewModel = GameSearchViewModel(gameRepository)

        viewModel.searchGames("")
        viewModel.searchGames("   ")

        assertEquals(0, viewModel.searchResults.first().size)
        assertFalse(viewModel.isSearching.first())
    }

    @Test
    fun clearSearch_clearsResultsAndSearching() = runTest {
        viewModel = GameSearchViewModel(gameRepository)

        viewModel.clearSearch()

        assertEquals(0, viewModel.searchResults.first().size)
        assertFalse(viewModel.isSearching.first())
    }
}
