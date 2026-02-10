package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.RegisterRepository
import com.example.glog.domain.repository.UserRepository
import com.example.glog.ui.state.UserUiState
import com.example.glog.ui.usecase.GetFavoriteGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val registerRepository: RegisterRepository,
    private val getFavoriteGamesUseCase: GetFavoriteGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userDeferred = async { userRepository.getUsers() }
            val favoritesDeferred = async { getFavoriteGamesUseCase() }

            val userResult = userDeferred.await()
            val favoritesResult = favoritesDeferred.await()

            when {
                userResult.isFailure -> {
                    _uiState.value = _uiState.value.copy(
                        error = userResult.exceptionOrNull()?.message ?: "Error al cargar usuario",
                        isLoading = false
                    )
                }
                else -> {
                    val user = userResult.getOrNull()?.firstOrNull()
                    val favoriteGames = favoritesResult.getOrNull() ?: emptyList()
                    val statsFromRegisters = if (user != null) loadStatsForUser(user.id.toLong()) else UserStats()
                    val favoritePlatform = mostFrequentPlatformFromGames(favoriteGames)
                    val stats = statsFromRegisters.copy(favoritePlatform = favoritePlatform)

                    _uiState.value = _uiState.value.copy(
                        user = user,
                        favoriteGames = favoriteGames,
                        stats = stats,
                        isLoading = false,
                        error = if (favoritesResult.isFailure) "No se pudieron cargar los juegos favoritos" else null
                    )
                }
            }
        }
    }

    private suspend fun loadStatsForUser(userId: Long): UserStats {
        return registerRepository.getRegistersByUser(userId).fold(
            onSuccess = { registers ->
                val totalHours = registers.sumOf { it.playtime ?: 0.0 }.toInt()
                val distinctGames = registers.mapNotNull { it.gameId }.toSet().size
                UserStats(
                    playTimeHours = totalHours,
                    distinctGames = distinctGames,
                    favoritePlatform = ""
                )
            },
            onFailure = { UserStats() }
        )
    }

    private fun mostFrequentPlatformFromGames(games: List<Game>): String {
        if (games.isEmpty()) return "—"
        val platformCounts = games
            .map { it.platformName?.takeIf { n -> n.isNotBlank() } ?: "Desconocida" }
            .groupingBy { it }
            .eachCount()
        return platformCounts.maxByOrNull { it.value }?.key ?: "—"
    }

    fun updateNickname(newNickname: String) {
        viewModelScope.launch {
            val user = _uiState.value.user ?: return@launch
            userRepository.updateUser(user.id.toLong(), newNickname.ifBlank { null }, user.image).fold(
                onSuccess = { updatedUser ->
                    _uiState.value = _uiState.value.copy(user = updatedUser, error = null)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message ?: "Error al actualizar nickname"
                    )
                }
            )
        }
    }
}



data class UserStats(
    val playTimeHours: Int = 42,
    val distinctGames: Int = 15,
    val favoritePlatform: String = "PC"
)