package com.example.pokeapitest.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PokemonWithDetails(
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId"
    )
    val varieties: List<PokemonVarietyEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId"
    )
    val moves: List<PokemonMoveEntity>
)
