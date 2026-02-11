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
    data class CreateRegister(
        val date: String? = null,
        val playtime: Double? = null,
        val gameId: Int? = null,
        val userId: Int? = null,
        val gameName: String? = null,
        val gameImageUrl: String? = null
    ) : RegisterEvent()
    data class UpdateRegister(
        val register: Register,
        val date: String?,
        val playtime: Double?,
        val gameId: Int?,
        val gameName: String?,
        val gameImageUrl: String?
    ) : RegisterEvent()
    data class DeleteRegister(val register: Register) : RegisterEvent()
}
