package com.example.pokeapitest.domain.model

import androidx.compose.ui.graphics.Color

enum class PokemonType(val typeName: String, val color: Color) {
    BUG("bug", Color(0xFF189B5C)),
    DRAGON("dragon", Color(0xFF33C6B4)),
    GRASS("grass", Color(0xFF26CB65)),
    STEEL("steel", Color(0xFF78DA9C)),
    DARK("dark", Color(0xFF3D3D3D)),
    FLYING("flying", Color(0xFF859AB1)),
    NORMAL("normal", Color(0xFFBC91BD)),
    GHOST("ghost", Color(0xFF8556A3)),
    ROCK("rock", Color(0xFF6E3F15)),
    GROUND("ground", Color(0xFF9A6738)),
    FIGHTING("fighting", Color(0xFFC1530F)),
    FIRE("fire", Color(0xFFF52211)),
    ELECTRIC("electric", Color(0xFFFEBF00)),
    POISON("poison", Color(0xFF724BF1)),
    PSYCHIC("psychic", Color(0xFFD500A2)),
    FAIRY("fairy", Color(0xFFF14D71)),
    WATER("water", Color(0xFF3B68F1)),
    ICE("ice", Color(0xFF92E1EE)),
    UNKNOWN("unknown", Color.Gray);

    companion object {
        fun fromString(type: String): PokemonType {
            return entries.find { it.typeName.equals(type, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
