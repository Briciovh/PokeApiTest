package com.example.pokeapitest.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonSpeciesDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "varieties") val varieties: List<PokemonVarietyDto>
)

@JsonClass(generateAdapter = true)
data class PokemonVarietyDto(
    @Json(name = "is_default") val isDefault: Boolean,
    @Json(name = "pokemon") val pokemon: PokemonResourceDto
)

@JsonClass(generateAdapter = true)
data class PokemonResourceDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
