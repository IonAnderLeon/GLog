package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.RegisterRepository
import com.example.glog.ui.state.RegisterEvent
import com.example.glog.ui.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.LoadRegisters -> loadRegisters()
            is RegisterEvent.SearchRegisters -> loadRegisters(event.query)
            is RegisterEvent.CreateRegister -> createRegister(
                date = event.date,
                playtime = event.playtime,
                gameId = event.gameId,
                userId = event.userId,
                gameName = event.gameName,
                gameImageUrl = event.gameImageUrl
            )
        }
    }

    private fun createRegister(
        date: String?,
        playtime: Double?,
        gameId: Int?,
        userId: Int?,
        gameName: String? = null,
        gameImageUrl: String? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val newRegister = com.example.glog.domain.model.Register(
                id = 0,
                date = date,
                playtime = playtime,
                gameId = gameId,
                gameName = gameName?.takeIf { it.isNotBlank() },
                gameImageUrl = gameImageUrl?.takeIf { it.isNotBlank() },
                userId = userId,
                userName = null
            )
            registerRepository.createRegister(newRegister).fold(
                onSuccess = { created ->
                    _state.value = _state.value.copy(
                        registers = listOf(created) + _state.value.registers,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun loadRegisters(search: String? = _state.value.searchQuery) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            registerRepository.getRegisters(search).fold(
                onSuccess = { registers ->
                    _state.value = _state.value.copy(
                        registers = registers,
                        searchQuery = search,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }
}
