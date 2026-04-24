package com.example.pokeapitest.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import org.junit.Rule
import org.junit.Test

class PokemonGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakePokemonList = listOf(
        PokemonListItem(id = 1, name = "bulbasaur", primaryType = PokemonType.GRASS),
        PokemonListItem(id = 4, name = "charmander", primaryType = PokemonType.FIRE),
        PokemonListItem(id = 7, name = "squirtle", primaryType = PokemonType.WATER)
    )

    @Test
    fun pokemonGrid_displaysItems() {
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonGrid(
                    pokemonList = fakePokemonList,
                    query = "",
                    onQueryChange = {},
                    onPokemonClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
        composeTestRule.onNodeWithText("Charmander").assertIsDisplayed()
        composeTestRule.onNodeWithText("Squirtle").assertIsDisplayed()
    }

    @Test
    fun pokemonGrid_filtersItemsByQuery() {
        var queryState = ""
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonGrid(
                    pokemonList = fakePokemonList,
                    query = queryState,
                    onQueryChange = { queryState = it },
                    onPokemonClick = {}
                )
            }
        }

        // Initially all shown (based on initial state passed to composable)
        composeTestRule.onNodeWithText("Bulbasaur").assertIsDisplayed()

        // In a real test, you'd want to use a state-backed composable or a ViewModel
        // But here we verify that if the query prop changes, the list filters.
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonGrid(
                    pokemonList = fakePokemonList,
                    query = "char",
                    onQueryChange = {},
                    onPokemonClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Charmander").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulbasaur").assertDoesNotExist()
    }
    
    @Test
    fun pokemonGrid_searchBar_updatesQuery() {
        var capturedQuery = ""
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonGrid(
                    pokemonList = emptyList(),
                    query = "",
                    onQueryChange = { capturedQuery = it },
                    onPokemonClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search Pokémon…")
            .performTextInput("Pikachu")
        
        assert(capturedQuery == "Pikachu")
    }
}
