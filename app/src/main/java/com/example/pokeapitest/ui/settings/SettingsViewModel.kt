package com.example.pokeapitest.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapitest.data.repository.SettingsRepository
import com.example.pokeapitest.domain.model.AppPreferences
import com.example.pokeapitest.domain.model.ImagePreference
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.domain.model.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = repository.getPreferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences()
        )

    fun updateTheme(theme: ThemePreference) {
        viewModelScope.launch {
            repository.updateTheme(theme)
        }
    }

    fun updateImagePreference(imagePreference: ImagePreference) {
        viewModelScope.launch {
            repository.updateImagePreference(imagePreference)
        }
    }

    fun updatePreferredType(type: PokemonType) {
        viewModelScope.launch {
            repository.updatePreferredType(type)
        }
    }
}
