package com.example.pokeapitest.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import org.junit.Rule
import org.junit.Test

class PokemonItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pokemonItem_displaysDataAndHandlesClicks() {
        var clickedName = ""
        val pokemon = PokemonListItem(id = 25, name = "pikachu", primaryType = PokemonType.ELECTRIC)
        
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonItem(
                    item = pokemon,
                    onItemClick = { clickedName = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Pikachu").assertIsDisplayed()
        composeTestRule.onNodeWithText("#025").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electric").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Pikachu").performClick()
        assert(clickedName == "pikachu")
    }
}
