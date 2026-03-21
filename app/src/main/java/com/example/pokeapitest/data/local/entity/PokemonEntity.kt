package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_details")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val frontDefault: String?,
    val types: String // Will store as a comma-separated string or JSON for simplicity
)
