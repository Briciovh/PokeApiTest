package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.pokeapitest.domain.model.PokemonType

@Entity(
    tableName = "pokemon_moves",
    foreignKeys = [
        ForeignKey(
            entity = PokemonEntity::class,
            parentColumns = ["id"],
            childColumns = ["pokemonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pokemonId")],
    primaryKeys = ["pokemonId", "name"]
)
data class PokemonMoveEntity(
    val pokemonId: Int,
    val name: String,
    val power: Int,
    val type: PokemonType
)
