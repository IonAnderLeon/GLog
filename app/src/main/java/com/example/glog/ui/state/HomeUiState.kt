package com.example.glog.ui.state

import com.example.glog.domain.model.Game

data class HomeUiState(
    val isLoading: Boolean = false,
    val searchText: String = "",
    val showSearchBar: Boolean = false,
    val recentGames: List<Game> = emptyList(),
    val popularGames: List<Game> = emptyList(),
    val recommendedGames: List<Game> = emptyList(),
    val newReleases: List<Game> = emptyList(),
    val classics: List<Game> = emptyList(),
    val error: String? = null
)