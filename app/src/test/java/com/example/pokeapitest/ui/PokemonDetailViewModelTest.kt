package com.example.pokeapitest.ui

import app.cash.turbine.test
import com.example.pokeapitest.MainDispatcherRule
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.use_case.GetPokemonDetailUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
    fun loadPokemonDetail_updatesPokemonDetailAndIsLoading_onSuccess() = runTest {
        val pokemonName = "bulbasaur"
        val expectedDetail = PokemonDetail(
            id = 1,
            name = pokemonName,
            height = 7,
            weight = 69,
            imageUrl = null,
            types = emptyList(),
            varieties = emptyList()
        )
        coEvery { getPokemonDetailUseCase(pokemonName) } returns flowOf(expectedDetail)

        viewModel.loadPokemonDetail(pokemonName)

        assertThat(viewModel.pokemonDetail.value).isEqualTo(expectedDetail)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun loadPokemonDetail_emitsErrorMessage_onFailure() = runTest {
        val pokemonName = "unknown"
        coEvery { getPokemonDetailUseCase(any()) } returns flow { throw Exception("Not found") }

        viewModel.errorChannel.test {
            viewModel.loadPokemonDetail(pokemonName)
            val error = awaitItem()
            assertThat(error).contains(pokemonName)
            assertThat(error).contains("Something went wrong")
        }

        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun retry_reloadsLastPokemon() = runTest {
        val pokemonName = "pikachu"
        coEvery { getPokemonDetailUseCase(pokemonName) } returns flowOf(null)

        viewModel.loadPokemonDetail(pokemonName)
        viewModel.retry()

        coVerify(exactly = 2) { getPokemonDetailUseCase(pokemonName) }
    }
}
