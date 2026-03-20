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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokeapitest.R
import com.example.pokeapitest.ui.theme.PokeApiTestTheme

sealed class Screen(val route: String) {
    object PokemonList : Screen("pokemon_list")
    object PokemonDetail : Screen("pokemon_detail/{name}") {
        fun createRoute(name: String) = "pokemon_detail/$name"
    }
}

@Composable
fun PokeAPINavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PokemonList.route) {
        composable(Screen.PokemonList.route) {
            val viewModel: PokemonViewModel = hiltViewModel()
            PokeAPIMainScreen(
                viewModel = viewModel,
                onPokemonClick = { name ->
                    navController.navigate(Screen.PokemonDetail.createRoute(name))
                }
            )
        }
        composable(
            route = Screen.PokemonDetail.route,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val viewModel: PokemonDetailViewModel = hiltViewModel()
            PokemonDetailScreen(name = name, viewModel = viewModel)
        }
    }
}

@Composable
fun PokeAPIMainScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit
) {
    PokeApiTestTheme {
        val names by viewModel.pokemonNames.collectAsState()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PokemonList(names, onPokemonClick)
        }
    }
}

@Composable
fun PokemonList(
    names: List<String>,
    onPokemonClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = dimensionResource(id = R.dimen.pokemon_list_vertical_padding),
                horizontal = dimensionResource(id = R.dimen.pokemon_list_horizontal_padding)
            )
    ) {
        items(names) { name ->
            PokemonItem(name = name, onItemClick = onPokemonClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonListPreview() {
    PokeApiTestTheme {
        PokemonList(
            names = listOf("Bulbasaur", "Ivysaur", "Venusaur", "Charmander"),
            onPokemonClick = {}
        )
    }
}
