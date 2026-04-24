package com.example.pokeapitest.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasText
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.model.PokemonMove
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.domain.model.PokemonVariety
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import org.junit.Rule
import org.junit.Test

class PokemonDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakePokemon = PokemonDetail(
        id = 6,
        name = "charizard",
        height = 17,
        weight = 905,
        imageUrl = "https://example.com/sprite.png",
        types = listOf(PokemonType.FIRE, PokemonType.FLYING),
        varieties = listOf(
            PokemonVariety("charizard", "url", true, "img"),
            PokemonVariety("charizard-mega", "url2", false, "img2")
        ),
        moves = (1..15).map { 
            PokemonMove("move-$it", 50, PokemonType.NORMAL)
        }
    )

    @Test
    fun pokemonDetailContent_displaysBasicInfo() {
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonDetailContent(pokemon = fakePokemon)
            }
        }

        composeTestRule.onNodeWithText("Charizard").assertIsDisplayed()
        composeTestRule.onNodeWithText("#006").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.7 m").assertIsDisplayed()
        composeTestRule.onNodeWithText("90.5 kg").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fire").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flying").assertIsDisplayed()
    }

    @Test
    fun pokemonDetailContent_movesSection_expandable() {
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonDetailContent(pokemon = fakePokemon)
            }
        }

        // Scroll to the moves section using the test tag of the LazyColumn
        composeTestRule.onNodeWithTag("pokemon_detail_list")
            .performScrollToNode(hasText("Moves (Sorted by Power)"))

        // Initially shows 10 moves
        composeTestRule.onNodeWithText("Move 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("More... (5 more)").assertIsDisplayed()
        
        // Expand
        composeTestRule.onNodeWithText("More... (5 more)").performClick()
        
        composeTestRule.waitForIdle()

        // Scroll to the new move if needed (it should be visible now)
        composeTestRule.onNodeWithTag("pokemon_detail_list")
            .performScrollToNode(hasText("Move 11"))
            
        composeTestRule.onNodeWithText("Move 11").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show Less").assertIsDisplayed()
        
        // Collapse
        composeTestRule.onNodeWithText("Show Less").performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag("pokemon_detail_list")
            .performScrollToNode(hasText("More... (5 more)"))

        composeTestRule.onNodeWithText("More... (5 more)").assertIsDisplayed()
    }

    @Test
    fun pokemonDetailContent_varietiesSection_displaysOtherVarieties() {
        composeTestRule.setContent {
            PokeApiTestTheme {
                PokemonDetailContent(pokemon = fakePokemon)
            }
        }

        // Scroll to the varieties section
        composeTestRule.onNodeWithTag("pokemon_detail_list")
            .performScrollToNode(hasText("Other Varieties"))
            
        composeTestRule.onNodeWithText("Other Varieties").assertIsDisplayed()
        composeTestRule.onNodeWithText("Charizard Mega").assertIsDisplayed()
    }
}
