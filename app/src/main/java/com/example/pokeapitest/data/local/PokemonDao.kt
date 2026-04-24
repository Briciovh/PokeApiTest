package com.example.pokeapitest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.local.entity.PokemonMoveEntity
import com.example.pokeapitest.data.local.entity.PokemonVarietyEntity
import com.example.pokeapitest.data.local.entity.PokemonWithDetails

@Dao
interface PokemonDao {

    // List operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonListItemEntity>)

    @Query("SELECT * FROM pokemon_list")
    suspend fun getPokemonList(): List<PokemonListItemEntity>

    @Query("SELECT * FROM pokemon_list WHERE id BETWEEN :startId AND :endId ORDER BY id")
    suspend fun getPokemonInRange(startId: Int, endId: Int): List<PokemonListItemEntity>

    @Query("SELECT COUNT(*) FROM pokemon_list WHERE id BETWEEN :startId AND :endId")
    suspend fun countInRange(startId: Int, endId: Int): Int

    @Query("DELETE FROM pokemon_list")
    suspend fun clearPokemonList()

    // Detail operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVarieties(varieties: List<PokemonVarietyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoves(moves: List<PokemonMoveEntity>)

    @Transaction
    suspend fun insertFullPokemonDetail(
        pokemon: PokemonEntity,
        varieties: List<PokemonVarietyEntity>,
        moves: List<PokemonMoveEntity>
    ) {
        insertPokemon(pokemon)
        insertVarieties(varieties)
        insertMoves(moves)
    }

    @Transaction
    @Query("SELECT * FROM pokemon_details WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonWithDetails?

    @Transaction
    @Query("SELECT * FROM pokemon_details WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonWithDetails?
}
