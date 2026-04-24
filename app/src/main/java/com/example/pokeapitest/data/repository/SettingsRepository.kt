package com.example.pokeapitest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pokeapitest.domain.model.AppPreferences
import com.example.pokeapitest.domain.model.ImagePreference
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingsRepository {
    fun getPreferences(): Flow<AppPreferences>
    suspend fun updateTheme(theme: ThemePreference)
    suspend fun updateImagePreference(imagePreference: ImagePreference)
    suspend fun updatePreferredType(type: PokemonType)
}

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val IMAGE_PREFERENCE = stringPreferencesKey("image_preference")
        val PREFERRED_TYPE = stringPreferencesKey("preferred_type")
    }

    override fun getPreferences(): Flow<AppPreferences> {
        return dataStore.data.map { preferences ->
            val theme = ThemePreference.valueOf(
                preferences[PreferencesKeys.THEME] ?: ThemePreference.SYSTEM.name
            )
            val imagePreference = ImagePreference.valueOf(
                preferences[PreferencesKeys.IMAGE_PREFERENCE] ?: ImagePreference.OFFICIAL.name
            )
            val preferredType = PokemonType.fromString(
                preferences[PreferencesKeys.PREFERRED_TYPE] ?: PokemonType.FIRE.typeName
            )
            AppPreferences(theme, imagePreference, preferredType)
        }
    }

    override suspend fun updateTheme(theme: ThemePreference) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    override suspend fun updateImagePreference(imagePreference: ImagePreference) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IMAGE_PREFERENCE] = imagePreference.name
        }
    }

    override suspend fun updatePreferredType(type: PokemonType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_TYPE] = type.typeName
        }
    }
}
