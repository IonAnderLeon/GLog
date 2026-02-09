package com.example.glog.ui.state

import com.example.glog.domain.model.Game

data class GameInfoUiState(
    val isLoading: Boolean = false,
    val game: Game? = null,
    val error: String? = null
)
