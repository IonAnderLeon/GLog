package com.example.glog.ui.event

sealed class HomeEvent {
    data object LoadHomeData : HomeEvent()
    data class OnSearchTextChange(val text: String) : HomeEvent()
    data object ToggleSearchBar : HomeEvent()
    data class OnGameClick(val gameId: String) : HomeEvent()
}