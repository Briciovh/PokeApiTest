package com.example.pokeapitest.ui

import app.cash.turbine.test
import com.example.pokeapitest.MainDispatcherRule
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.domain.use_case.GetPokemonListUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.*
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
        coEvery { getPokemonListUseCase(any(), any()) } returns flowOf(emptyList())
        viewModel = PokemonViewModel(getPokemonListUseCase)
    }

    @Test
    fun loadPokemonByGeneration_updatesPokemonListAndIsLoading_onSuccess() = runTest {
        val expectedItems = listOf(
            PokemonListItem(id = 25, name = "pikachu", primaryType = PokemonType.ELECTRIC),
            PokemonListItem(id = 1, name = "bulbasaur", primaryType = PokemonType.GRASS)
        )
        coEvery { getPokemonListUseCase(any(), any()) } returns flowOf(expectedItems)

        viewModel.loadPokemonByGeneration(1, 151)

        assertThat(viewModel.pokemonList.value).isEqualTo(expectedItems)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun loadPokemonByGeneration_emitsErrorMessage_onFailure() = runTest {
        val errorMessage = "Network error"
        coEvery { getPokemonListUseCase(any(), any()) } returns flow { throw Exception(errorMessage) }

        viewModel.loadPokemonByGeneration(1, 151)

        viewModel.errorChannel.test {
            val error = awaitItem()
            assertThat(error).contains(errorMessage)
        }

        assertThat(viewModel.isLoading.value).isFalse()
    }
}
