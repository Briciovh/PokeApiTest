package com.example.pokeapitest.ui

import com.example.pokeapitest.MainDispatcherRule
import com.example.pokeapitest.domain.use_case.GetPokemonListUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        coEvery { getPokemonListUseCase() } returns emptyList()
    }

    @Test
    fun `loadPokemon updates pokemonNames and isLoading`() = runTest {
        val expectedNames = listOf("Pikachu", "Bulbasaur")
        coEvery { getPokemonListUseCase() } returns expectedNames

        viewModel = PokemonViewModel(getPokemonListUseCase)

        assertThat(viewModel.pokemonNames.value).isEqualTo(expectedNames)
        assertThat(viewModel.isLoading.value).isFalse()
    }
}
