package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.data.remote.dto.TypeDto
import com.example.pokeapitest.data.remote.dto.TypeSlotDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val dao: PokemonDao
) : PokemonRepository {

    override fun getPokemonList(limit: Int): Flow<List<PokemonListItemEntity>> = flow {
        // 1. Get from DB
        val localList = dao.getPokemonList()
        emit(localList)

        // 2. If empty or stale (simple check for now), fetch from API
        if (localList.isEmpty()) {
            try {
                val remoteList = api.getPokemonList(limit).results.map {
                    PokemonListItemEntity(name = it.name, url = it.url)
                }
                dao.clearPokemonList()
                dao.insertPokemonList(remoteList)
                emit(dao.getPokemonList())
            } catch (e: Exception) {
                // Error handling handled by ViewModel via SharedFlow
                throw e
            }
        }
    }

    override fun getPokemonDetail(name: String): Flow<PokemonDto?> = flow {
        // 1. Check local DB
        val localPokemon = dao.getPokemonByName(name)
        if (localPokemon != null) {
            emit(localPokemon.toDto())
            return@flow
        }

        // 2. Fetch from API if missing or to update
        try {
            val remotePokemon = api.getPokemonDetail(name)
            dao.insertPokemon(remotePokemon.toEntity())
            emit(dao.getPokemonByName(name)?.toDto())
        } catch (e: Exception) {
            if (localPokemon == null) throw e
        }
    }
}

// Mapper extensions
fun PokemonDto.toEntity() = PokemonEntity(
    id = id,
    name = name,
    height = height,
    weight = weight,
    frontDefault = sprites.frontDefault,
    types = pokemonTypes
)

fun PokemonEntity.toDto() = PokemonDto(
    id = id,
    name = name,
    height = height,
    weight = weight,
    sprites = SpritesDto(frontDefault = frontDefault),
    types = types.mapIndexed { index, pokemonType ->
        TypeSlotDto(
            slot = index + 1,
            type = TypeDto(name = pokemonType.typeName, url = "")
        )
    }
)

interface PokemonRepository {
    fun getPokemonList(limit: Int): Flow<List<PokemonListItemEntity>>
    fun getPokemonDetail(name: String): Flow<PokemonDto?>
}
