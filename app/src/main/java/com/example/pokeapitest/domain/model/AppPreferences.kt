package com.example.pokeapitest.domain.model

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

enum class ImagePreference {
    OFFICIAL, PIXEL
}

data class AppPreferences(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val imagePreference: ImagePreference = ImagePreference.OFFICIAL
)
