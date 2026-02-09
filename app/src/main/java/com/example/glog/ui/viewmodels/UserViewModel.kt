package com.example.glog.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.UserRepository

import com.example.glog.ui.state.GameInfoUiState
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
    private val getFavoriteGamesUseCase: GetFavoriteGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Cargar usuario y favoritos en paralelo
            val userDeferred = async { userRepository.getUsers() }
            val favoritesDeferred = async { getFavoriteGamesUseCase() }

            val userResult = userDeferred.await()
            val favoritesResult = favoritesDeferred.await()

            // Combinar resultados
            when {
                userResult.isFailure -> {
                    // Error al cargar usuario
                    _uiState.value = _uiState.value.copy(
                        error = userResult.exceptionOrNull()?.message ?: "Error al cargar usuario",
                        isLoading = false
                    )
                }
                favoritesResult.isFailure -> {
                    // Usuario OK, pero error en favoritos
                    val user = userResult.getOrNull()?.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        favoriteGames = emptyList(),
                        error = "No se pudieron cargar los juegos favoritos",
                        isLoading = false
                    )
                }
                else -> {
                    // Ambos OK
                    val user = userResult.getOrNull()?.firstOrNull()
                    val favoriteGames = favoritesResult.getOrNull() ?: emptyList()

                    _uiState.value = _uiState.value.copy(
                        user = user,
                        favoriteGames = favoriteGames,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }
}



data class UserStats(
    val playTimeHours: Int = 42,
    val distinctGames: Int = 15,
    val favoritePlatform: String = "PC"
)