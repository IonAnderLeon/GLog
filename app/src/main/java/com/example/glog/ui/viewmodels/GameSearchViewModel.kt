package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.model.Game
import com.example.glog.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameSearchViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Game>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    fun searchGames(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            _isSearching.value = true
            gameRepository.getGames(query).fold(
                onSuccess = { games ->
                    _searchResults.value = games
                },
                onFailure = {
                    _searchResults.value = emptyList()
                }
            )
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    companion object {
        private const val DEBOUNCE_MS = 350L
    }
}
