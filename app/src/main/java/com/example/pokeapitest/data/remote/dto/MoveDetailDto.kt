package com.example.pokeapitest.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoveDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "power") val power: Int?,
    @Json(name = "type") val type: MoveTypeDto
)

@JsonClass(generateAdapter = true)
data class MoveTypeDto(
    @Json(name = "name") val name: String
)
