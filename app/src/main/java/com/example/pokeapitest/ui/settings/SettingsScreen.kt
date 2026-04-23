package com.example.pokeapitest.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.pokeapitest.domain.model.ImagePreference
import com.example.pokeapitest.domain.model.ThemePreference

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val preferences by viewModel.preferences.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        ThemeOption(
            selected = preferences.theme == ThemePreference.LIGHT,
            label = "Light Mode",
            onClick = { viewModel.updateTheme(ThemePreference.LIGHT) }
        )
        ThemeOption(
            selected = preferences.theme == ThemePreference.DARK,
            label = "Dark Mode",
            onClick = { viewModel.updateTheme(ThemePreference.DARK) }
        )
        ThemeOption(
            selected = preferences.theme == ThemePreference.SYSTEM,
            label = "System Default",
            onClick = { viewModel.updateTheme(ThemePreference.SYSTEM) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Image Preference",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        ImagePreferenceOption(
            selected = preferences.imagePreference == ImagePreference.OFFICIAL,
            label = "Official Artwork",
            onClick = { viewModel.updateImagePreference(ImagePreference.OFFICIAL) }
        )
        ImagePreferenceOption(
            selected = preferences.imagePreference == ImagePreference.PIXEL,
            label = "Pixel Art",
            onClick = { viewModel.updateImagePreference(ImagePreference.PIXEL) }
        )
    }
}

@Composable
fun ThemeOption(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // handled by selectable
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ImagePreferenceOption(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // handled by selectable
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
