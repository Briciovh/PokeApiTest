package com.example.pokeapitest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.model.PokemonVariety
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

    val backgroundColor = pokemonDetail?.pokemonTypes?.firstOrNull()?.color?.copy(alpha = 0.5f) 
        ?: MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        pokemonDetail?.let { pokemon ->
            PokemonDetailContent(pokemon)
        }
        LoadingOverlay(isLoading = isLoading)
    }
}

@Composable
fun PokemonDetailContent(pokemon: PokemonDetail) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        text = pokemon.types.joinToString(", ") { it.typeName.capitalizeWords() },
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

            if (pokemon.varieties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Varieties:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(pokemon.varieties) { variety ->
            VarietyItem(variety)
        }
    }
}

@Composable
fun VarietyItem(variety: PokemonVariety) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = variety.imageUrl,
            contentDescription = variety.name,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = variety.name.replace("-", " ").capitalizeWords(),
            style = MaterialTheme.typography.bodyMedium
        )
        if (variety.isDefault) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(Default)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailContentPreview() {
    PokeApiTestTheme {
        PokemonDetailContent(
            pokemon = PokemonDetail(
                id = 25,
                name = "pikachu",
                height = 4,
                weight = 60,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                types = listOf(com.example.pokeapitest.domain.model.PokemonType.ELECTRIC),
                varieties = listOf(
                    PokemonVariety(
                        name = "pikachu",
                        url = "https://pokeapi.co/api/v2/pokemon/25/",
                        isDefault = true,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
                    ),
                    PokemonVariety(
                        name = "pikachu-rock-star",
                        url = "https://pokeapi.co/api/v2/pokemon/10080/",
                        isDefault = false,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/10080.png"
                    )
                )
            )
        )
    }
}
