package com.example.pokeapitest.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.pokeapitest.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PokeAPINavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun drawerNavigation_toSettings_andBack() {
        // 1. Open drawer
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        
        // 2. Click Settings in drawer
        // Use a more specific matcher to avoid ambiguity with TopAppBar title
        composeTestRule.onNode(hasText("Settings") and hasAnyAncestor(hasTestTag("ModalDrawerSheet"))).performClick()
        
        // 3. Verify on Settings screen (check TopAppBar title)
        composeTestRule.onNodeWithTag("top_app_bar_title").assertTextEquals("Settings")
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        
        // 4. Go back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // 5. Verify back on List screen (Kanto)
        composeTestRule.onNodeWithTag("top_app_bar_title").assertTextEquals("Kanto")
    }

    @Test
    fun drawerNavigation_toJohtoGeneration() {
        // 1. Open drawer
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        
        // 2. Click Johto in drawer
        composeTestRule.onNode(hasText("Johto") and hasAnyAncestor(hasTestTag("ModalDrawerSheet"))).performClick()
        
        // 3. Verify Title changed
        composeTestRule.onNodeWithTag("top_app_bar_title").assertTextEquals("Johto")
    }

    @Test
    fun navigateToPokemonDetail_andBack() {
        // Wait for a pokemon to appear in the list
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Bulbasaur", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Click on Bulbasaur
        composeTestRule.onNodeWithText("Bulbasaur", ignoreCase = true).performClick()

        // 2. Verify on Detail screen
        composeTestRule.onNodeWithTag("top_app_bar_title").assertTextEquals("Details")
        
        // Wait for Bulbasaur to appear on the Detail screen (after network fetch)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Bulbasaur", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Bulbasaur", ignoreCase = true).assertIsDisplayed()

        // 3. Go back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 4. Verify back on List
        composeTestRule.onNodeWithTag("top_app_bar_title").assertTextEquals("Kanto")
    }
}
