package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.local.entity.PokemonMoveEntity
import com.example.pokeapitest.data.local.entity.PokemonVarietyEntity
import com.example.pokeapitest.data.local.entity.PokemonWithDetails
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.MoveDetailDto
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonSpeciesDto
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonMove
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.domain.model.PokemonVariety
import com.example.pokeapitest.util.pokemonOfficialArtworkShinyUrl
import com.example.pokeapitest.util.pokemonOfficialArtworkUrl
import com.example.pokeapitest.util.pokemonPixelArtShinyUrl
import com.example.pokeapitest.util.pokemonPixelArtUrl
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

    override fun getPokemonList(startId: Int, endId: Int): Flow<List<PokemonListItemEntity>> = flow {
        val expectedCount = endId - startId + 1

        // 1. Emit whatever is already cached for this range
        val cached = dao.getPokemonInRange(startId, endId)
        emit(cached)

        // 2. Fetch from API only if cache is incomplete for this generation
        if (cached.size < expectedCount) {
            try {
                val remoteBasicList = api.getPokemonList(
                    limit = expectedCount,
                    offset = startId - 1
                ).results

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
                                PokemonListItemEntity(name = basicItem.name, url = basicItem.url)
                            }
                        }
                    }.awaitAll()
                }

                dao.insertPokemonList(detailedList)
                emit(dao.getPokemonInRange(startId, endId))
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun getPokemonDetail(name: String): Flow<PokemonDetail?> = flow {
        // 1. Check local DB
        val localPokemon = dao.getPokemonByName(name)
        // If we have local data and it has moves (our new requirement), emit it
        if (localPokemon != null && localPokemon.moves.isNotEmpty()) {
            emit(localPokemon.toDomain())
            return@flow
        }

        // 2. Fetch from API if missing or incomplete
        try {
            coroutineScope {
                val pokemonDeferred = async { api.getPokemonDetail(name) }
                val speciesDeferred = async { api.getPokemonSpecies(name) }

                val pokemonDto = pokemonDeferred.await()
                val speciesDto = speciesDeferred.await()

                // Fetch details for all moves in parallel
                val moveDetails = pokemonDto.moves.map { moveSlot ->
                    async {
                        try {
                            api.getMoveDetail(moveSlot.move.name)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                val (entity, varieties, moves) = pokemonDto.toEntityData(speciesDto, moveDetails)
                dao.insertFullPokemonDetail(entity, varieties, moves)
                emit(dao.getPokemonByName(name)?.toDomain())
            }
        } catch (e: Exception) {
            if (localPokemon == null) throw e
            else emit(localPokemon.toDomain())
        }
    }
}

// Mapper extensions
data class PokemonEntityData(
    val entity: PokemonEntity,
    val varieties: List<PokemonVarietyEntity>,
    val moves: List<PokemonMoveEntity>
)

fun PokemonDto.toEntityData(
    species: PokemonSpeciesDto,
    moveDetails: List<MoveDetailDto>
): PokemonEntityData {
    val entity = PokemonEntity(
        id = id,
        name = name,
        height = height,
        weight = weight,
        frontDefault = sprites.frontDefault,
        types = pokemonTypes
    )

    val varietyEntities = species.varieties.map { variety ->
        val varId = variety.pokemon.url.split("/").filter { it.isNotEmpty() }.lastOrNull()?.toIntOrNull()
        val pixelUrl = varId?.let { pokemonPixelArtUrl(it) } ?: ""
        val artworkUrl = varId?.let { pokemonOfficialArtworkUrl(it) } ?: ""
        PokemonVarietyEntity(
            pokemonId = id,
            name = variety.pokemon.name,
            url = variety.pokemon.url,
            isDefault = variety.isDefault,
            imageUrl = pixelUrl,
            officialArtworkUrl = artworkUrl
        )
    }

    val moveEntities = moveDetails.map {
        PokemonMoveEntity(
            pokemonId = id,
            name = it.name,
            power = it.power ?: 0,
            type = PokemonType.fromString(it.type.name)
        )
    }

    return PokemonEntityData(entity, varietyEntities, moveEntities)
}

fun PokemonWithDetails.toDomain() = PokemonDetail(
    id = pokemon.id,
    name = pokemon.name,
    height = pokemon.height,
    weight = pokemon.weight,
    imageUrl = pokemon.frontDefault,
    officialArtworkUrl = pokemonOfficialArtworkUrl(pokemon.id),
    types = pokemon.types,
    varieties = varieties.map {
        PokemonVariety(
            name = it.name,
            url = it.url,
            isDefault = it.isDefault,
            imageUrl = it.imageUrl,
            officialArtworkUrl = it.officialArtworkUrl
        )
    }.let { list ->
        val shiny = PokemonVariety(
            name = "${pokemon.name}-shiny",
            url = "",
            isDefault = false,
            imageUrl = pokemonPixelArtShinyUrl(pokemon.id),
            officialArtworkUrl = pokemonOfficialArtworkShinyUrl(pokemon.id),
            isShiny = true
        )
        val defaultIndex = list.indexOfFirst { it.isDefault }
        if (defaultIndex != -1) {
            list.toMutableList().apply { add(defaultIndex + 1, shiny) }
        } else {
            list + shiny
        }
    },
    moves = moves.map {
        PokemonMove(
            name = it.name,
            power = it.power,
            type = it.type
        )
    }.sortedWith(compareByDescending<PokemonMove> { it.power }.thenBy { it.name })
)

fun PokemonListItemEntity.toDomain() = PokemonListItem(
    id = id,
    name = name,
    primaryType = primaryType
)

interface PokemonRepository {
    fun getPokemonList(startId: Int, endId: Int): Flow<List<PokemonListItemEntity>>
    fun getPokemonDetail(name: String): Flow<PokemonDetail?>
}
