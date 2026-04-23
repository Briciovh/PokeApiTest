package com.example.pokeapitest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokeapitest.domain.model.ImagePreference
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.ui.theme.LocalImagePreference
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import com.example.pokeapitest.util.capitalizeWords
import com.example.pokeapitest.util.pokemonOfficialArtworkUrl
import com.example.pokeapitest.util.pokemonPixelArtUrl


@Composable
fun PokemonItem(
    item: PokemonListItem,
    onItemClick: (String) -> Unit
) {
    val typeColor = item.primaryType.color
    val imagePreference = LocalImagePreference.current
    val spriteUrl = if (imagePreference == ImagePreference.OFFICIAL) {
        pokemonOfficialArtworkUrl(item.id)
    } else {
        pokemonPixelArtUrl(item.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clickable { onItemClick(item.name) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = typeColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative circle watermark (bottom-right)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ID + type badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${item.id.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.28f)
                    ) {
                        Text(
                            text = item.primaryType.typeName.capitalizeWords(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Sprite centered
                AsyncImage(
                    model = spriteUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    filterQuality = if (spriteUrl.contains("official-artwork")) FilterQuality.Low else FilterQuality.None,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Name at bottom
                Text(
                    text = item.name.capitalizeWords(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonItemPreview() {
    PokeApiTestTheme {
        PokemonItem(
            item = PokemonListItem(id = 6, name = "charizard", primaryType = PokemonType.FIRE),
            onItemClick = {}
        )
    }
}
