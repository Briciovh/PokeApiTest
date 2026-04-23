package com.example.pokeapitest.data.repository

import app.cash.turbine.test
import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import com.example.pokeapitest.data.remote.dto.PokemonListItemDto
import com.example.pokeapitest.data.remote.dto.PokemonResourceDto
import com.example.pokeapitest.data.remote.dto.PokemonSpeciesDto
import com.example.pokeapitest.data.remote.dto.PokemonVarietyDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
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
            sprites = SpritesDto(frontDefault = "front_url"), types = emptyList()
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
        val localEntity = PokemonEntity(
            id = 1,
            name = name,
            height = 7,
            weight = 69,
            frontDefault = "front_url",
            types = listOf(PokemonType.GRASS, PokemonType.POISON),
            varieties = "bulbasaur|https://pokeapi.co/api/v2/pokemon/1/|true" +
                "|https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png" +
                "|https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
        )

        coEvery { dao.getPokemonByName(name) } returns localEntity

        repository.getPokemonDetail(name).test {
            // First emission from DB
            val firstEmission = awaitItem()
            assertThat(firstEmission?.name).isEqualTo(name)
            assertThat(firstEmission?.imageUrl).isEqualTo("front_url")
            assertThat(firstEmission?.officialArtworkUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png")
            assertThat(firstEmission?.varieties).hasSize(1)
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
            types = emptyList()
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

        coEvery { dao.getPokemonByName(name) } returns null andThen remoteDto.toEntity(remoteSpeciesDto)
        coEvery { api.getPokemonDetail(name) } returns remoteDto
        coEvery { api.getPokemonSpecies(name) } returns remoteSpeciesDto

        repository.getPokemonDetail(name).test {
            val result = awaitItem()
            assertThat(result?.name).isEqualTo(name)
            assertThat(result?.officialArtworkUrl)
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png")
            awaitComplete()
        }

        coVerify(exactly = 1) { dao.insertPokemon(any()) }
    }
}
