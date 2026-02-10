package com.example.glog.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

private object Keys {
    val USE_DARK_THEME = booleanPreferencesKey("use_dark_theme")
    val USE_LARGE_TEXT = booleanPreferencesKey("use_large_text")
}

class AppPreferencesDataSource(private val context: Context) {

    val preferences: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            useDarkTheme = prefs[Keys.USE_DARK_THEME],
            useLargeText = prefs[Keys.USE_LARGE_TEXT] ?: false
        )
    }

    suspend fun setDarkTheme(useDark: Boolean) {
        context.dataStore.edit { it[Keys.USE_DARK_THEME] = useDark }
    }

    suspend fun setLargeText(useLarge: Boolean) {
        context.dataStore.edit { it[Keys.USE_LARGE_TEXT] = useLarge }
    }
}
