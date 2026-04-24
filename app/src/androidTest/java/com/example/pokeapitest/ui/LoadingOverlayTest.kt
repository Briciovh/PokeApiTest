package com.example.pokeapitest.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

class LoadingOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingOverlay_whenIsLoadingTrue_isDisplayed() {
        composeTestRule.setContent {
            LoadingOverlay(isLoading = true)
        }

        composeTestRule.onNodeWithTag("loading_overlay").assertIsDisplayed()
    }

    @Test
    fun loadingOverlay_whenIsLoadingFalse_isNotDisplayed() {
        composeTestRule.setContent {
            LoadingOverlay(isLoading = false)
        }

        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }
}
