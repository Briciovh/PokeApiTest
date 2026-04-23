package com.example.pokeapitest.data.remote.dto

import com.example.pokeapitest.domain.model.PokemonType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "height") val height: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "sprites") val sprites: SpritesDto,
    @Json(name = "types") val types: List<TypeSlotDto>,
    @Json(name = "moves") val moves: List<MoveSlotDto>
) {
    val pokemonTypes: List<PokemonType>
        get() = types.map { PokemonType.fromString(it.type.name) }
}

@JsonClass(generateAdapter = true)
data class MoveSlotDto(
    @Json(name = "move") val move: MoveDto
)

@JsonClass(generateAdapter = true)
data class MoveDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class SpritesDto(
    @Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class TypeSlotDto(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: TypeDto
)

@JsonClass(generateAdapter = true)
data class TypeDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
