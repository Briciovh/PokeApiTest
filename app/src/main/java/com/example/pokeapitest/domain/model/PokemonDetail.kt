package com.example.pokeapitest.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val imageUrl: String?,
    val officialArtworkUrl: String? = null,
    val types: List<PokemonType>,
    val varieties: List<PokemonVariety>
) {
    val pokemonTypes: List<PokemonType>
        get() = types
}

data class PokemonVariety(
    val name: String,
    val url: String,
    val isDefault: Boolean,
    val imageUrl: String? = null,
    val officialArtworkUrl: String? = null
)
