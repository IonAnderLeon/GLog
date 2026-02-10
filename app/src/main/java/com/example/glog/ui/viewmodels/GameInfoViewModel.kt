package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.CollectionRepository
import com.example.glog.domain.repository.GameRepository
import com.example.glog.ui.state.GameInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FAVORITES_COLLECTION_ID = 1L

@HiltViewModel
class GameInfoViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun loadGame(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            gameRepository.getGameById(gameId.toLong()).fold(
                onSuccess = { game ->
                    val isInFavorites = collectionRepository.getCollectionById(FAVORITES_COLLECTION_ID)
                        .getOrNull()
                        ?.games
                        ?.any { it.id == game.id } == true
                    _uiState.value = _uiState.value.copy(
                        game = game,
                        isLoading = false,
                        isInFavorites = isInFavorites
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun toggleFavorites() {
        val game = _uiState.value.game ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingFavorites = true)
            val isCurrentlyInFavorites = _uiState.value.isInFavorites
            val result = if (isCurrentlyInFavorites) {
                collectionRepository.removeGameFromCollection(FAVORITES_COLLECTION_ID, game.id)
            } else {
                collectionRepository.addGameToCollection(FAVORITES_COLLECTION_ID, game.id)
            }
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isInFavorites = !isCurrentlyInFavorites,
                        isUpdatingFavorites = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isUpdatingFavorites = false)
                }
            )
        }
    }
}