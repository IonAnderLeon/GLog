package com.example.glog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.glog.data.preferences.AppPreferences
import com.example.glog.ui.screens.MainScreen
import com.example.glog.ui.theme.GLogTheme
import com.example.glog.ui.viewmodels.AppPreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appPrefs: AppPreferencesViewModel = hiltViewModel()
            val prefs by appPrefs.preferences.collectAsStateWithLifecycle(initialValue = AppPreferences())
            GLogTheme(
                darkTheme = prefs.useDarkTheme ?: isSystemInDarkTheme(),
                useLargeText = prefs.useLargeText,
                content = { MainScreen() }
            )
        }
    }
}
