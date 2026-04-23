package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokeapitest.domain.model.PokemonType

@Entity(tableName = "pokemon_details")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val frontDefault: String?,
    val types: List<PokemonType>,
    val varieties: String, // Will store as a JSON string for simplicity or comma-separated names|url|isDefault
    val moves: String // name|power|type;...
)
