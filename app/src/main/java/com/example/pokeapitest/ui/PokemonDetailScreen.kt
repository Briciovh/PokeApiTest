package com.example.pokeapitest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.data.remote.dto.TypeDto
import com.example.pokeapitest.data.remote.dto.TypeSlotDto
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import com.example.pokeapitest.util.capitalizeWords

@Composable
fun PokemonDetailScreen(
    name: String,
    viewModel: PokemonDetailViewModel
) {
    val pokemonDetail by viewModel.pokemonDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = name) {
        viewModel.loadPokemonDetail(name)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        pokemonDetail?.let { pokemon ->
            PokemonDetailContent(pokemon)
        }
        LoadingOverlay(isLoading = isLoading)
    }
}

@Composable
fun PokemonDetailContent(pokemon: PokemonDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = pokemon.sprites.frontDefault,
            contentDescription = pokemon.name,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pokemon.name.capitalizeWords(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ID: #${pokemon.id}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Types:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = pokemon.types.joinToString(", ") { it.type.name.capitalizeWords() },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Height: ${pokemon.height / 10.0} m",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Weight: ${pokemon.weight / 10.0} kg",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailContentPreview() {
    PokeApiTestTheme {
        PokemonDetailContent(
            pokemon = PokemonDto(
                id = 25,
                name = "pikachu",
                height = 4,
                weight = 60,
                sprites = SpritesDto(
                    frontDefault = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
                ),
                types = listOf(
                    TypeSlotDto(
                        slot = 1,
                        type = TypeDto(name = "electric", url = "")
                    )
                )
            )
        )
    }
}
