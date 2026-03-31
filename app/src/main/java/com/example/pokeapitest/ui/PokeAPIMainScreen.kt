package com.example.pokeapitest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokeapitest.R
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import kotlinx.coroutines.flow.collectLatest

sealed class Screen(val route: String) {
    object PokemonList : Screen("pokemon_list")
    object PokemonDetail : Screen("pokemon_detail/{name}") {
        fun createRoute(name: String) = "pokemon_detail/$name"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeAPINavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = navController.previousBackStackEntry != null
    val snackbarHostState = remember { SnackbarHostState() }

    PokeApiTestTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pokedex") },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.PokemonList.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.PokemonList.route) {
                    val viewModel: PokemonViewModel = hiltViewModel()
                    
                    LaunchedEffect(key1 = true) {
                        viewModel.errorChannel.collectLatest { error ->
                            snackbarHostState.showSnackbar(error)
                        }
                    }

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

                    LaunchedEffect(key1 = true) {
                        viewModel.errorChannel.collectLatest { error ->
                            snackbarHostState.showSnackbar(error)
                        }
                    }

                    PokemonDetailScreen(name = name, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PokeAPIMainScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PokemonList(pokemonList, onPokemonClick)
            LoadingOverlay(isLoading = isLoading)
        }
    }
}

@Composable
fun PokemonList(
    pokemonList: List<PokemonListItem>,
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
        items(pokemonList) { item ->
            PokemonItem(item = item, onItemClick = onPokemonClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonListPreview() {
    PokeApiTestTheme {
        PokemonList(
            pokemonList = listOf(
                PokemonListItem(id = 1, name = "bulbasaur", primaryType = PokemonType.GRASS),
                PokemonListItem(id = 4, name = "charmander", primaryType = PokemonType.FIRE)
            ),
            onPokemonClick = {}
        )
    }
}
