package com.example.glog.ui.state

import com.example.glog.domain.model.Game

data class GameInfoUiState(
    val isLoading: Boolean = false,
    val game: Game? = null,
    val error: String? = null,
    val isInFavorites: Boolean = false,
    val isUpdatingFavorites: Boolean = false,
    val messageForToast: String? = null,
    val similarGames: List<Game> = emptyList()
)
