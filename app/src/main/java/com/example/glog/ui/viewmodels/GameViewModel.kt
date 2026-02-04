package com.example.glog.ui.viewmodels

import com.example.glog.ui.state.GamesEvent
import com.example.glog.ui.state.GamesState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(GamesState())
    val state: State<GamesState> = _state

    fun onEvent(event: GamesEvent) {
        when(event) {
            is GamesEvent.LoadGames -> loadGames()
        }
    }

    private fun loadGames() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getGamesUseCase().onSuccess { games ->
                _state.value = _state.value.copy(
                    games = games,
                    isLoading = false
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    error = error.message,
                    isLoading = false
                )
            }
        }
    }
}

Repositorio: