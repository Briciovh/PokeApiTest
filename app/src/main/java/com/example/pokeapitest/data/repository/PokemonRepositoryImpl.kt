package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonSpeciesDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.data.remote.dto.TypeDto
import com.example.pokeapitest.data.remote.dto.TypeSlotDto
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.model.PokemonVariety
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
                throw e
            }
        }
    }

    override fun getPokemonDetail(name: String): Flow<PokemonDetail?> = flow {
        // 1. Check local DB
        val localPokemon = dao.getPokemonByName(name)
        if (localPokemon != null) {
            emit(localPokemon.toDomain())
            return@flow
        }

        // 2. Fetch from API if missing
        try {
            coroutineScope {
                val pokemonDeferred = async { api.getPokemonDetail(name) }
                val speciesDeferred = async { api.getPokemonSpecies(name) }

                val pokemonDto = pokemonDeferred.await()
                val speciesDto = speciesDeferred.await()

                val entity = pokemonDto.toEntity(speciesDto)
                dao.insertPokemon(entity)
                emit(dao.getPokemonByName(name)?.toDomain())
            }
        } catch (e: Exception) {
            if (localPokemon == null) throw e
        }
    }
}

// Mapper extensions
fun PokemonDto.toEntity(species: PokemonSpeciesDto) = PokemonEntity(
    id = id,
    name = name,
    height = height,
    weight = weight,
    frontDefault = sprites.frontDefault,
    types = types.joinToString(",") { it.type.name },
    varieties = species.varieties.joinToString(";") { "${it.pokemon.name}|${it.pokemon.url}|${it.isDefault}" }
)

fun PokemonEntity.toDomain() = PokemonDetail(
    id = id,
    name = name,
    height = height,
    weight = weight,
    imageUrl = frontDefault,
    types = types.split(","),
    varieties = if (varieties.isEmpty()) emptyList() else varieties.split(";").map {
        val parts = it.split("|")
        PokemonVariety(
            name = parts[0],
            url = parts[1],
            isDefault = parts[2].toBoolean()
        )
    }
)

interface PokemonRepository {
    fun getPokemonList(limit: Int): Flow<List<PokemonListItemEntity>>
    fun getPokemonDetail(name: String): Flow<PokemonDetail?>
}
