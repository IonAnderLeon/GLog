package com.example.glog.ui.state

import com.example.glog.domain.model.Register

data class RegisterState(
    val registers: List<Register> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String? = null
)

sealed class RegisterEvent {
    object LoadRegisters : RegisterEvent()
    data class SearchRegisters(val query: String?) : RegisterEvent()
}
