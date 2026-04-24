package com.example.pokeapitest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "pokemon_varieties",
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
data class PokemonVarietyEntity(
    val pokemonId: Int,
    val name: String,
    val url: String,
    val isDefault: Boolean,
    val imageUrl: String?,
    val officialArtworkUrl: String?
)
