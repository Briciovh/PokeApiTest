package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class PokemonRepositoryImplTest {

    private lateinit var repository: PokemonRepositoryImpl
    private val api: PokeApi = mockk()

    @Before
    fun setUp() {
        repository = PokemonRepositoryImpl(api)
    }

    @Test
    fun `getPokemonList returns expected data`() = runTest {
        val expectedDto = PokemonListDto(results = emptyList())
        coEvery { api.getPokemonList(any()) } returns expectedDto

        val result = repository.getPokemonList(151)

        assertThat(result).isEqualTo(expectedDto)
    }

    @Test
    fun `getPokemonList throws exception when api fails`() = runTest {
        coEvery { api.getPokemonList(any()) } throws Exception("Network error")

        assertThrows(Exception::class.java) {
            runTest { repository.getPokemonList(151) }
        }
    }

    @Test
    fun `getPokemonDetail returns expected data`() = runTest {
        val expectedDto = PokemonDto(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = SpritesDto(frontDefault = null),
            types = emptyList()
        )
        coEvery { api.getPokemonDetail(any()) } returns expectedDto

        val result = repository.getPokemonDetail("bulbasaur")

        assertThat(result).isEqualTo(expectedDto)
    }

    @Test
    fun `getPokemonDetail throws exception when api fails`() = runTest {
        coEvery { api.getPokemonDetail(any()) } throws Exception("Not found")

        assertThrows(Exception::class.java) {
            runTest { repository.getPokemonDetail("bulbasaur") }
        }
    }
}
