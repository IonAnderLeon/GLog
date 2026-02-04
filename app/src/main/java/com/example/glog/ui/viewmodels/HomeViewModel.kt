package com.example.glog.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.GameRepository
import com.example.glog.ui.event.HomeEvent
import com.example.glog.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//Ejemplo de viewmodel, cambiar después

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    var state by mutableStateOf(HomeUiState())
        private set

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeEvent) = when (event) {
        HomeEvent.ReloadData -> loadHomeData()
        is HomeEvent.OnSearchTextChange -> state = state.copy(searchText = event.text)
        HomeEvent.ToggleSearchBar -> state = state.copy(showSearchBar = !state.showSearchBar)
        is HomeEvent.OnGameClick -> Unit // UI maneja navegación
    }

    private fun loadHomeData() = viewModelScope.launch {
        state = state.copy(isLoading = true)

        val recentResult = gameRepository.getRecentGames()
        val popularResult = gameRepository.getPopularGames()
        val recommendedResult = gameRepository.getRecommendedGames()

        // Combinar resultados
        state = state.copy(
            recentGames = recentResult.getOrDefault(emptyList()),
            popularGames = popularResult.getOrDefault(emptyList()),
            recommendedGames = recommendedResult.getOrDefault(emptyList()),
            isLoading = false,
            error = listOf(recentResult, popularResult, recommendedResult)
                .firstOrNull { it.isFailure }
                ?.exceptionOrNull()
                ?.message
        )
    }
}