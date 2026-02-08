package com.example.glog.ui.state

import com.example.glog.domain.model.Game

data class GamesState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class GamesEvent {
    object LoadGames : GamesEvent()
    data class SearchGames(val query: String) : GamesEvent()
}
