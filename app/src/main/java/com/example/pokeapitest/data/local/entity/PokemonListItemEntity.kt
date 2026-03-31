package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokeapitest.domain.model.PokemonType

@Entity(tableName = "pokemon_list")
data class PokemonListItemEntity(
    @PrimaryKey val name: String,
    val url: String,
    val id: Int = 0,
    val primaryType: PokemonType = PokemonType.UNKNOWN
)
