package com.example.pokeapitest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.model.PokemonType
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

    Box(modifier = Modifier.fillMaxSize()) {
        pokemonDetail?.let { pokemon ->
            PokemonDetailContent(pokemon)
        }
        LoadingOverlay(isLoading = isLoading)
    }
}

@Composable
fun PokemonDetailContent(pokemon: PokemonDetail) {
    val primaryColor = pokemon.types.firstOrNull()?.color ?: Color(0xFFCC0000)
    val secondaryColor = pokemon.types.getOrNull(1)?.color ?: primaryColor.copy(alpha = 0.6f)

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Type-gradient hero ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
        ) {
            // Faint large ID watermark
            Text(
                text = "#${pokemon.id.toString().padStart(3, '0')}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 16.dp)
            )

            // Official artwork
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        // ── Rounded white bottom sheet ────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Name + type chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pokemon.name.capitalizeWords(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pokemon.types.forEach { type -> PokemonTypeChip(type) }
                        }
                    }
                }

                // Height / Weight card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = primaryColor.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PokemonStatCell(
                                label = "Height",
                                value = "${"%.1f".format(pokemon.height / 10.0)} m"
                            )
                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = primaryColor.copy(alpha = 0.3f)
                            )
                            PokemonStatCell(
                                label = "Weight",
                                value = "${"%.1f".format(pokemon.weight / 10.0)} kg"
                            )
                        }
                    }
                }

                // Varieties section
                if (pokemon.varieties.isNotEmpty()) {
                    item {
                        Text(
                            text = "Varieties",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(pokemon.varieties) { variety ->
                                PokemonVarietyCard(variety = variety, accentColor = primaryColor)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun PokemonTypeChip(type: PokemonType) {
    Surface(
        shape = RoundedCornerShape(50),
        color = type.color
    ) {
        Text(
            text = type.typeName.capitalizeWords(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PokemonStatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun PokemonVarietyCard(variety: PokemonVariety, accentColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = variety.imageUrl,
                contentDescription = variety.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = variety.name.replace("-", " ").capitalizeWords(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            if (variety.isDefault) {
                Text(
                    text = "Default",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820)
@Composable
fun PokemonDetailContentPreview() {
    PokeApiTestTheme {
        PokemonDetailContent(
            pokemon = PokemonDetail(
                id = 6,
                name = "charizard",
                height = 17,
                weight = 905,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/6.png",
                types = listOf(PokemonType.FIRE, PokemonType.FLYING),
                varieties = listOf(
                    PokemonVariety(
                        name = "charizard",
                        url = "",
                        isDefault = true,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"
                    ),
                    PokemonVariety(
                        name = "charizard-mega-x",
                        url = "",
                        isDefault = false,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/10034.png"
                    )
                )
            )
        )
    }
}
