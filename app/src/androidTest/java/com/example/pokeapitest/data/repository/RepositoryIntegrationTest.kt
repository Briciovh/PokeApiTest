package com.example.pokeapitest.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.PokemonDatabase
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.*
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryIntegrationTest {

    private lateinit var database: PokemonDatabase
    private lateinit var dao: PokemonDao
    private lateinit var repository: PokemonRepositoryImpl
    private val api: PokeApi = mockk()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PokemonDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.dao
        repository = PokemonRepositoryImpl(api, dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getPokemonList_fullFlow_fetchesFromRemoteAndSavesToLocal() = runBlocking {
        val pokemonName = "bulbasaur"
        coEvery { api.getPokemonList(any(), any()) } returns PokemonListDto(
            results = listOf(PokemonListItemDto(name = pokemonName, url = "url1"))
        )
        coEvery { api.getPokemonDetail(pokemonName) } returns PokemonDto(
            id = 1,
            name = pokemonName,
            height = 7,
            weight = 69,
            sprites = SpritesDto(frontDefault = "sprite_url"),
            types = emptyList(),
            moves = emptyList()
        )

        repository.getPokemonList(1, 1).test {
            awaitItem() // First emission (might be empty)
            val result = awaitItem()
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo(pokemonName)
            awaitComplete()
        }

        val cached = dao.getPokemonList()
        assertThat(cached).hasSize(1)
        assertThat(cached[0].name).isEqualTo(pokemonName)
    }

    @Test
    fun getPokemonDetail_fullFlow_fetchesAndPersists() = runBlocking {
        val name = "charmander"
        val remoteDto = PokemonDto(
            id = 4, name = name, height = 6, weight = 85,
            sprites = SpritesDto(frontDefault = "charmander_sprite"),
            types = emptyList(), moves = emptyList()
        )
        val speciesDto = PokemonSpeciesDto(
            id = 4, name = name,
            varieties = listOf(
                PokemonVarietyDto(true, PokemonResourceDto(name, "url"))
            )
        )

        coEvery { api.getPokemonDetail(name) } returns remoteDto
        coEvery { api.getPokemonSpecies(name) } returns speciesDto

        repository.getPokemonDetail(name).test {
            val result = awaitItem()
            assertThat(result?.name).isEqualTo(name)
            awaitComplete()
        }

        val cached = dao.getPokemonByName(name)
        assertThat(cached).isNotNull()
        assertThat(cached?.id).isEqualTo(4)
    }
}
