package com.example.glog.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.GameRepository

import com.example.glog.ui.state.GameInfoUiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameInfoViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun loadGame(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)  // Usa _uiState.value

            gameRepository.getGameById(gameId.toLong()).fold(
                onSuccess = { game ->
                    _uiState.value = _uiState.value.copy(  // Usa _uiState.value
                        game = game,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(  // Usa _uiState.value
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }
}