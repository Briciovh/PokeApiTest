package com.example.pokeapitest.ui

import app.cash.turbine.test
import com.example.pokeapitest.MainDispatcherRule
import com.example.pokeapitest.domain.use_case.GetPokemonListUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PokemonViewModel
    private val getPokemonListUseCase: GetPokemonListUseCase = mockk()

    @Before
    fun setUp() {
        // Mocking use case for init block
        coEvery { getPokemonListUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun loadPokemon_updatesPokemonNamesAndIsLoading_onSuccess() = runTest {
        val expectedNames = listOf("Pikachu", "Bulbasaur")
        coEvery { getPokemonListUseCase() } returns flowOf(expectedNames)

        viewModel = PokemonViewModel(getPokemonListUseCase)

        assertThat(viewModel.pokemonNames.value).isEqualTo(expectedNames)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun loadPokemon_emitsErrorMessage_onFailure() = runTest {
        val errorMessage = "Network error"
        coEvery { getPokemonListUseCase() } returns flow { throw Exception(errorMessage) }

        viewModel = PokemonViewModel(getPokemonListUseCase)

        viewModel.errorChannel.test {
            val error = awaitItem()
            assertThat(error).contains(errorMessage)
        }
        
        assertThat(viewModel.isLoading.value).isFalse()
    }
}
