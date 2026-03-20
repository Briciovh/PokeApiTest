package com.example.pokeapitest.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonListDto(
    @Json(name = "results") val results: List<PokemonListItemDto>
)

@JsonClass(generateAdapter = true)
data class PokemonListItemDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
