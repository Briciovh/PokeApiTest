package com.example.pokeapitest.ui

import com.example.pokeapitest.MainDispatcherRule
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.domain.use_case.GetPokemonDetailUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PokemonDetailViewModel
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase = mockk()

    @Before
    fun setUp() {
        viewModel = PokemonDetailViewModel(getPokemonDetailUseCase)
    }

    @Test
    fun `loadPokemonDetail updates pokemonDetail and isLoading`() = runTest {
        val pokemonName = "bulbasaur"
        val expectedDetail = PokemonDto(
            id = 1,
            name = pokemonName,
            height = 7,
            weight = 69,
            sprites = SpritesDto(frontDefault = null),
            types = emptyList()
        )
        coEvery { getPokemonDetailUseCase(pokemonName) } returns expectedDetail

        viewModel.loadPokemonDetail(pokemonName)

        assertThat(viewModel.pokemonDetail.value).isEqualTo(expectedDetail)
        assertThat(viewModel.isLoading.value).isFalse()
    }
}
