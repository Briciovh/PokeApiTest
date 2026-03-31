package com.example.pokeapitest.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val imageUrl: String?,
    val types: List<String>,
    val varieties: List<PokemonVariety>
)

data class PokemonVariety(
    val name: String,
    val url: String,
    val isDefault: Boolean
)
