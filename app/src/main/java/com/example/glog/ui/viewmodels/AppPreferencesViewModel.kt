package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.data.preferences.AppPreferences
import com.example.glog.data.preferences.AppPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    private val preferencesDataSource: AppPreferencesDataSource
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = preferencesDataSource.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences()
        )

    fun setDarkTheme(useDark: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setDarkTheme(useDark)
        }
    }

    fun setLargeText(useLarge: Boolean) {
        viewModelScope.launch {
            preferencesDataSource.setLargeText(useLarge)
        }
    }
}
