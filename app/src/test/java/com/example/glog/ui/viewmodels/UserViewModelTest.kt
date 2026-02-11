package com.example.glog.ui.viewmodels

import com.example.glog.MainCoroutineRule
import com.example.glog.domain.model.Game
import com.example.glog.domain.model.User
import com.example.glog.domain.repository.RegisterRepository
import com.example.glog.domain.repository.UserRepository
import com.example.glog.ui.usecase.GetFavoriteGamesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class UserViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val userRepository: UserRepository = mockk()
    private val registerRepository: RegisterRepository = mockk()
    private val getFavoriteGamesUseCase: GetFavoriteGamesUseCase = mockk()
    private lateinit var viewModel: UserViewModel

    @Test
    fun loadUserData_success_setsUserAndFavorites() = runTest {
        val user = User(1, "Gamer", "https://avatar.com/1.png")
        val favorites = listOf(
            Game(1, "Zelda", null, 2023, 4.5, "Switch", "Aventura", null)
        )
        coEvery { userRepository.getUsers() } returns Result.success(listOf(user))
        coEvery { getFavoriteGamesUseCase() } returns Result.success(favorites)
        coEvery { registerRepository.getRegistersByUser(1L) } returns Result.success(emptyList())
        viewModel = UserViewModel(userRepository, registerRepository, getFavoriteGamesUseCase)

        viewModel.loadUserData()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(user, state.user)
        assertEquals(1, state.favoriteGames.size)
        assertEquals("Zelda", state.favoriteGames.first().title)
    }

    @Test
    fun loadUserData_userFails_setsError() = runTest {
        coEvery { userRepository.getUsers() } returns Result.failure(RuntimeException("Network error"))
        coEvery { getFavoriteGamesUseCase() } returns Result.success(emptyList())
        viewModel = UserViewModel(userRepository, registerRepository, getFavoriteGamesUseCase)

        viewModel.loadUserData()

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun loadUserData_favoritesFail_stillSetsUserAndShowsFavoritesError() = runTest {
        val user = User(1, "Gamer", null)
        coEvery { userRepository.getUsers() } returns Result.success(listOf(user))
        coEvery { getFavoriteGamesUseCase() } returns Result.failure(RuntimeException("Fav error"))
        coEvery { registerRepository.getRegistersByUser(1L) } returns Result.success(emptyList())
        viewModel = UserViewModel(userRepository, registerRepository, getFavoriteGamesUseCase)

        viewModel.loadUserData()

        val state = viewModel.uiState.first()
        assertEquals(user, state.user)
        assertEquals("No se pudieron cargar los juegos favoritos", state.error)
    }
}
