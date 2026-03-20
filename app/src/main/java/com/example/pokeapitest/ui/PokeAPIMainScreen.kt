package com.example.pokeapitest.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokeapitest.R
import com.example.pokeapitest.ui.theme.PokeApiTestTheme

@Composable
fun PokeAPIMainScreen(viewModel: PokemonViewModel) {
    PokeApiTestTheme {
        val names by viewModel.pokemonNames.collectAsState()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PokemonList(names)
        }
    }
}

@Composable
fun PokemonList(names: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = dimensionResource(id = R.dimen.pokemon_list_vertical_padding),
                horizontal = dimensionResource(id = R.dimen.pokemon_list_horizontal_padding)
            )
    ) {
        items(names) { name ->
            PokemonItem(name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonListPreview() {
    PokeApiTestTheme {
        PokemonList(names = listOf("Bulbasaur", "Ivysaur", "Venusaur", "Charmander"))
    }
}
