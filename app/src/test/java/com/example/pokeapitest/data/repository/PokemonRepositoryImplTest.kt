package com.example.pokeapitest.data.repository

import app.cash.turbine.test
import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.local.entity.PokemonMoveEntity
import com.example.pokeapitest.data.local.entity.PokemonVarietyEntity
import com.example.pokeapitest.data.local.entity.PokemonWithDetails
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.MoveDetailDto
import com.example.pokeapitest.data.remote.dto.MoveDto
import com.example.pokeapitest.data.remote.dto.MoveSlotDto
import com.example.pokeapitest.data.remote.dto.MoveTypeDto
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import com.example.pokeapitest.data.remote.dto.PokemonListItemDto
import com.example.pokeapitest.data.remote.dto.PokemonResourceDto
import com.example.pokeapitest.data.remote.dto.PokemonSpeciesDto
import com.example.pokeapitest.data.remote.dto.PokemonVarietyDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.util.pokemonOfficialArtworkUrl
import com.example.pokeapitest.util.pokemonPixelArtUrl
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PokemonRepositoryImplTest {

    private lateinit var repository: PokemonRepositoryImpl
    private val api: PokeApi = mockk()
    private val dao: PokemonDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = PokemonRepositoryImpl(api, dao)
    }

    @Test
    fun getPokemonList_emitsLocalDataFirst_thenFetchesRemoteIfCacheIncomplete() = runTest {
        val startId = 1
        val endId = 151
        val fetched = listOf(PokemonListItemEntity(name = "bulbasaur", url = "url1", id = 1))

        // Cache empty on first call, populated after insert
        coEvery { dao.getPokemonInRange(startId, endId) } returns emptyList() andThen fetched

        coEvery { api.getPokemonList(limit = 151, offset = 0) } returns PokemonListDto(
            results = listOf(PokemonListItemDto(name = "bulbasaur", url = "url1"))
        )
        coEvery { api.getPokemonDetail("bulbasaur") } returns PokemonDto(
            id = 1, name = "bulbasaur", height = 7, weight = 69,
            sprites = SpritesDto(frontDefault = "front_url"), types = emptyList(), moves = emptyList()
        )

        repository.getPokemonList(startId, endId).test {
            assertThat(awaitItem()).isEmpty()
            val second = awaitItem()
            assertThat(second).hasSize(1)
            assertThat(second[0].name).isEqualTo("bulbasaur")
            awaitComplete()
        }

        coVerify(exactly = 1) { dao.insertPokemonList(any()) }
    }

    @Test
    fun getPokemonList_onlyEmitsLocalData_ifCacheComplete() = runTest {
        val startId = 1
        val endId = 3
        // All 3 pokemon present → cache complete, no API call
        val cached = listOf(
            PokemonListItemEntity(name = "bulbasaur", url = "url1", id = 1),
            PokemonListItemEntity(name = "ivysaur",   url = "url2", id = 2),
            PokemonListItemEntity(name = "venusaur",  url = "url3", id = 3)
        )
        coEvery { dao.getPokemonInRange(startId, endId) } returns cached

        repository.getPokemonList(startId, endId).test {
            val result = awaitItem()
            assertThat(result).hasSize(3)
            awaitComplete()
        }

        coVerify(exactly = 0) { api.getPokemonList(any(), any()) }
    }

    @Test
    fun getPokemonDetail_onlyEmitsLocalData_ifPresent() = runTest {
        val name = "bulbasaur"
        val pokemon = PokemonEntity(
            id = 1,
            name = name,
            height = 7,
            weight = 69,
            frontDefault = "front_url",
            types = listOf(PokemonType.GRASS, PokemonType.POISON)
        )
        val varieties = listOf(
            PokemonVarietyEntity(
                pokemonId = 1,
                name = "bulbasaur",
                url = "https://pokeapi.co/api/v2/pokemon/1/",
                isDefault = true,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                officialArtworkUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
            )
        )
        val moves = listOf(
            PokemonMoveEntity(
                pokemonId = 1,
                name = "tackle",
                power = 40,
                type = PokemonType.NORMAL
            )
        )
        val localWithDetails = PokemonWithDetails(pokemon, varieties, moves)

        coEvery { dao.getPokemonByName(name) } returns localWithDetails

        repository.getPokemonDetail(name).test {
            // First emission from DB
            val firstEmission = awaitItem()
            assertThat(firstEmission?.name).isEqualTo(name)
            assertThat(firstEmission?.imageUrl).isEqualTo("front_url")
            assertThat(firstEmission?.officialArtworkUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png")
            assertThat(firstEmission?.varieties).hasSize(2) // original + injected shiny
            assertThat(firstEmission?.varieties?.get(0)?.name).isEqualTo("bulbasaur")
            assertThat(firstEmission?.varieties?.get(0)?.imageUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png")
            assertThat(firstEmission?.varieties?.get(0)?.officialArtworkUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png")

            awaitComplete()
        }

        coVerify(exactly = 0) { api.getPokemonDetail(any()) }
    }

    @Test
    fun getPokemonDetail_fetchesFromRemote_ifLocalIsMissing() = runTest {
        val name = "bulbasaur"
        
        val remoteDto = PokemonDto(
            id = 1,
            name = name,
            height = 7,
            weight = 69,
            sprites = SpritesDto(frontDefault = "front_url"),
            types = emptyList(),
            moves = emptyList()
        )
        
        val remoteSpeciesDto = PokemonSpeciesDto(
            id = 1,
            name = name,
            varieties = listOf(
                PokemonVarietyDto(
                    isDefault = true,
                    pokemon = PokemonResourceDto(name = name, url = "https://pokeapi.co/api/v2/pokemon/1/")
                )
            )
        )

        val entityData = remoteDto.toEntityData(remoteSpeciesDto, emptyList())
        val withDetails = PokemonWithDetails(entityData.entity, entityData.varieties, entityData.moves)

        coEvery { dao.getPokemonByName(name) } returns null andThen withDetails
        coEvery { api.getPokemonDetail(name) } returns remoteDto
        coEvery { api.getPokemonSpecies(name) } returns remoteSpeciesDto

        repository.getPokemonDetail(name).test {
            val result = awaitItem()
            assertThat(result?.name).isEqualTo(name)
            assertThat(result?.officialArtworkUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png")
            awaitComplete()
        }

        coVerify(exactly = 1) { dao.insertFullPokemonDetail(any(), any(), any()) }
    }

    @Test
    fun getPokemonDetail_fallsBackToLocal_whenAPIFails() = runTest {
        val name = "pikachu"
        val pokemon = PokemonEntity(
            id = 25, name = name, height = 4, weight = 60,
            frontDefault = null, types = listOf(PokemonType.ELECTRIC)
        )
        val varieties = listOf(
            PokemonVarietyEntity(
                pokemonId = 25,
                name = "pikachu",
                url = "https://pokeapi.co/api/v2/pokemon/25/",
                isDefault = true,
                imageUrl = pokemonPixelArtUrl(25),
                officialArtworkUrl = pokemonOfficialArtworkUrl(25)
            )
        )
        val localWithDetails = PokemonWithDetails(pokemon, varieties, emptyList())

        coEvery { dao.getPokemonByName(name) } returns localWithDetails
        coEvery { api.getPokemonDetail(name) } throws RuntimeException("Network error")
        coEvery { api.getPokemonSpecies(name) } throws RuntimeException("Network error")

        repository.getPokemonDetail(name).test {
            val result = awaitItem()
            assertThat(result?.name).isEqualTo(name)
            assertThat(result?.moves).isEmpty()
            awaitComplete()
        }

        coVerify(exactly = 0) { dao.insertFullPokemonDetail(any(), any(), any()) }
    }

    @Test
    fun getPokemonDetail_fetchesMoveDetailsInParallel_forEachMove() = runTest {
        val name = "pikachu"
        val remoteDto = PokemonDto(
            id = 25, name = name, height = 4, weight = 60,
            sprites = SpritesDto(frontDefault = null),
            types = emptyList(),
            moves = listOf(
                MoveSlotDto(MoveDto(name = "tackle", url = "tackle-url")),
                MoveSlotDto(MoveDto(name = "growl",  url = "growl-url"))
            )
        )
        val speciesDto = PokemonSpeciesDto(
            id = 25, name = name,
            varieties = listOf(
                PokemonVarietyDto(
                    isDefault = true,
                    pokemon = PokemonResourceDto(name = name, url = "https://pokeapi.co/api/v2/pokemon/25/")
                )
            )
        )

        coEvery { dao.getPokemonByName(name) } returns null
        coEvery { api.getPokemonDetail(name) } returns remoteDto
        coEvery { api.getPokemonSpecies(name) } returns speciesDto
        coEvery { api.getMoveDetail("tackle") } returns MoveDetailDto(1, "tackle", 40, MoveTypeDto("normal"))
        coEvery { api.getMoveDetail("growl")  } returns MoveDetailDto(2, "growl",  null, MoveTypeDto("normal"))

        repository.getPokemonDetail(name).test {
            awaitItem()
            awaitComplete()
        }

        coVerify(exactly = 1) { api.getMoveDetail("tackle") }
        coVerify(exactly = 1) { api.getMoveDetail("growl") }
    }

    @Test
    fun getPokemonList_skipsItem_whenDetailFetchThrows() = runTest {
        val startId = 1
        val endId = 2
        val afterInsert = listOf(
            PokemonListItemEntity(name = "bulbasaur", url = "url1", id = 1, primaryType = PokemonType.GRASS),
            PokemonListItemEntity(name = "bad-mon",   url = "url2")
        )

        coEvery { dao.getPokemonInRange(startId, endId) } returns emptyList() andThen afterInsert
        coEvery { api.getPokemonList(limit = 2, offset = 0) } returns PokemonListDto(
            results = listOf(
                PokemonListItemDto(name = "bulbasaur", url = "url1"),
                PokemonListItemDto(name = "bad-mon",   url = "url2")
            )
        )
        coEvery { api.getPokemonDetail("bulbasaur") } returns PokemonDto(
            id = 1, name = "bulbasaur", height = 7, weight = 69,
            sprites = SpritesDto(frontDefault = null), types = emptyList(), moves = emptyList()
        )
        coEvery { api.getPokemonDetail("bad-mon") } throws RuntimeException("not found")

        repository.getPokemonList(startId, endId).test {
            assertThat(awaitItem()).isEmpty()
            assertThat(awaitItem()).hasSize(2)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            dao.insertPokemonList(match { list ->
                list.any { it.name == "bulbasaur" && it.id == 1 } &&
                list.any { it.name == "bad-mon" && it.id == 0 }
            })
        }
    }
}
