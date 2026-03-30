package com.example.pokeapitest.data.repository

import app.cash.turbine.test
import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import com.example.pokeapitest.data.remote.dto.PokemonListItemDto
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
    fun `getPokemonList emits local data first then remote if local is empty`() = runTest {
        // Mock local DB empty initially
        coEvery { dao.getPokemonList() } returns emptyList() andThen listOf(
            PokemonListItemEntity(name = "bulbasaur", url = "url1")
        )
        
        // Mock remote API call
        val remoteList = PokemonListDto(
            results = listOf(PokemonListItemDto(name = "bulbasaur", url = "url1"))
        )
        coEvery { api.getPokemonList(any()) } returns remoteList

        repository.getPokemonList(151).test {
            // First emission: empty local list
            val firstEmission = awaitItem()
            assertThat(firstEmission).isEmpty()

            // Second emission: list after API fetch and save
            val secondEmission = awaitItem()
            assertThat(secondEmission).hasSize(1)
            assertThat(secondEmission[0].name).isEqualTo("bulbasaur")
            
            awaitComplete()
        }

        coVerify(exactly = 1) { dao.clearPokemonList() }
        coVerify(exactly = 1) { dao.insertPokemonList(any()) }
    }

    @Test
    fun `getPokemonList only emits local data if not empty`() = runTest {
        // Mock local DB not empty
        val localList = listOf(
            PokemonListItemEntity(name = "bulbasaur", url = "url1")
        )
        coEvery { dao.getPokemonList() } returns localList

        repository.getPokemonList(151).test {
            val result = awaitItem()
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("bulbasaur")
            awaitComplete()
        }

        coVerify(exactly = 0) { api.getPokemonList(any()) }
    }

    @Test
    fun `getPokemonDetail only emits local data if present`() = runTest {
        val name = "bulbasaur"
        val localEntity = PokemonEntity(
            id = 1,
            name = name,
            height = 7,
            weight = 69,
            frontDefault = "front_url",
            types = "grass,poison"
        )
        
        coEvery { dao.getPokemonByName(name) } returns localEntity

        repository.getPokemonDetail(name).test {
            // First emission from DB
            val firstEmission = awaitItem()
            assertThat(firstEmission?.name).isEqualTo(name)
            assertThat(firstEmission?.sprites?.frontDefault).isEqualTo("front_url")

            awaitComplete()
        }

        coVerify(exactly = 0) { api.getPokemonDetail(any()) }
    }

    @Test
    fun `getPokemonDetail fetches from remote if local is missing`() = runTest {
        val name = "bulbasaur"
        
        val remoteDto = PokemonDto(
            id = 1,
            name = name,
            height = 7,
            weight = 69,
            sprites = SpritesDto(frontDefault = "front_url"),
            types = emptyList()
        )

        coEvery { dao.getPokemonByName(name) } returns null andThen remoteDto.toEntity()
        coEvery { api.getPokemonDetail(name) } returns remoteDto

        repository.getPokemonDetail(name).test {
            // Should NOT emit null initially, then emit remote data
            val result = awaitItem()
            assertThat(result?.name).isEqualTo(name)
            awaitComplete()
        }

        coVerify(exactly = 1) { dao.insertPokemon(any()) }
    }
}
