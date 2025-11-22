package com.ltsw.animo.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    }

    /**
     * Get the current dark mode setting as a Flow
     */
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false // Default to light mode
        }

    /**
     * Get the current dynamic color setting as a Flow
     */
    val isDynamicColorEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] ?: true // Default to enabled
        }

    /**
     * Save dark mode preference
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Save dynamic color preference
     */
    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }
}

