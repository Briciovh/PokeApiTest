package com.example.pokeapitest.domain.model

import com.example.pokeapitest.domain.model.PokemonType

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

enum class ImagePreference {
    OFFICIAL, PIXEL
}

data class AppPreferences(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val imagePreference: ImagePreference = ImagePreference.OFFICIAL,
    val preferredType: PokemonType = PokemonType.FIRE
)
