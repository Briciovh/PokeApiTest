package com.example.pokeapitest.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pokeapitest.domain.model.AppPreferences
import com.example.pokeapitest.domain.model.ThemePreference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: SettingsViewModel = mockk(relaxed = true)

    @Test
    fun settingsScreen_displaysOptions() {
        val prefs = AppPreferences(theme = ThemePreference.DARK)
        every { viewModel.preferences } returns MutableStateFlow(prefs)

        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Mode").assertIsSelected()
        
        composeTestRule.onNodeWithText("Image Preference").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preferred Type").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_themeSelection_callsViewModel() {
        val prefs = AppPreferences(theme = ThemePreference.LIGHT)
        every { viewModel.preferences } returns MutableStateFlow(prefs)

        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Dark Mode").performClick()
        
        verify { viewModel.updateTheme(ThemePreference.DARK) }
    }
}
