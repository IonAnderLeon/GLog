package com.example.glog.ui.state

import com.example.glog.domain.model.Game
import com.example.glog.domain.model.User
import com.example.glog.ui.viewmodels.UserStats

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val favoriteGames: List<Game> = emptyList(),
    val stats: UserStats = UserStats(),
    val error: String? = null
)
