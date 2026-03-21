package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list")
data class PokemonListItemEntity(
    @PrimaryKey val name: String,
    val url: String
)
