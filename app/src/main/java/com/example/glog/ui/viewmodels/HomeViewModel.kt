package com.example.glog.ui.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.GameRepository

import com.example.glog.ui.state.HomeUiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            gameRepository.getAllGames().fold(
                onSuccess = { games ->
                    _uiState.value = _uiState.value.copy(
                        recentGames = games.take(10),
                        popularGames = games.sortedByDescending { it.rating }.take(10),
                        isLoading = false
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

    fun onSearchTextChange(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
    }

    fun onToggleSearch() {
        _uiState.value = _uiState.value.copy(
            showSearchBar = !_uiState.value.showSearchBar
        )
    }
}