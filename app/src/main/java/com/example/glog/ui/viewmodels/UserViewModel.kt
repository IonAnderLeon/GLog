package com.example.glog.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.UserRepository

import com.example.glog.ui.state.GameInfoUiState
import com.example.glog.ui.state.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            userRepository.getUsers().fold(
                onSuccess = { users ->
                    // Por ahora tomamos el primer usuario
                    val user = users.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        user = user,
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
}



data class UserStats(
    val playTimeHours: Int = 42,
    val distinctGames: Int = 15,
    val favoritePlatform: String = "PC"
)