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
import com.example.pokeapitest.domain.model.PokemonListItem

import com.example.pokeapitest.domain.model.PokemonType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

        // 2. If empty fetch from API and concurrently fetch details
        if (localList.isEmpty()) {
            try {
                val remoteBasicList = api.getPokemonList(limit).results

                // Concurrently fetch details for each pokemon to get ID and Type
                val detailedList = coroutineScope {
                    remoteBasicList.map { basicItem ->
                        async {
                            try {
                                val details = api.getPokemonDetail(basicItem.name)
                                PokemonListItemEntity(
                                    name = basicItem.name,
                                    url = basicItem.url,
                                    id = details.id,
                                    primaryType = details.pokemonTypes.firstOrNull() ?: PokemonType.UNKNOWN
                                )
                            } catch (e: Exception) {
                                // Fallback for failed detail fetch
                                PokemonListItemEntity(name = basicItem.name, url = basicItem.url)
                            }
                        }
                    }.awaitAll()
                }

                dao.clearPokemonList()
                dao.insertPokemonList(detailedList)
                emit(dao.getPokemonList())
            } catch (e: Exception) {
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

fun PokemonListItemEntity.toDomain() = PokemonListItem(
    id = id,
    name = name,
    primaryType = primaryType
)

interface PokemonRepository {
    fun getPokemonList(limit: Int): Flow<List<PokemonListItemEntity>>
    fun getPokemonDetail(name: String): Flow<PokemonDto?>
}
