package com.example.pokeapitest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity

@Database(
    entities = [PokemonListItemEntity::class, PokemonEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val dao: PokemonDao
}
