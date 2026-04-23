package com.example.pokeapitest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokeapitest.domain.model.AppPreferences
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.ui.settings.SettingsScreen
import com.example.pokeapitest.ui.settings.SettingsViewModel
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object PokemonList : Screen("pokemon_list?gen={gen}") {
        fun createRoute(gen: Int) = "pokemon_list?gen=$gen"
    }
    object Settings : Screen("settings")
    object PokemonDetail : Screen("pokemon_detail/{name}") {
        fun createRoute(name: String) = "pokemon_detail/$name"
    }
}

data class GenerationInfo(
    val id: Int,
    val name: String,
    val startId: Int,
    val endId: Int
)

val Generations = listOf(
    GenerationInfo(1, "Gen 1", 1, 151),
    GenerationInfo(2, "Gen 2", 152, 251),
    GenerationInfo(3, "Gen 3", 252, 386)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeAPINavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val canNavigateBack = navBackStackEntry != null && navController.previousBackStackEntry != null
    val snackbarHostState = remember { SnackbarHostState() }

    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val preferences by settingsViewModel.preferences.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    PokeApiTestTheme(
        themePreference = preferences.theme,
        imagePreference = preferences.imagePreference
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "Pokédex",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    HorizontalDivider()
                    Generations.forEach { gen ->
                        NavigationDrawerItem(
                            label = { Text(gen.name) },
                            selected = currentRoute?.startsWith("pokemon_list") == true && 
                                       navBackStackEntry?.arguments?.getInt("gen") == gen.id,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(Screen.PokemonList.createRoute(gen.id)) {
                                    popUpTo(Screen.PokemonList.route) { inclusive = true }
                                }
                            },
                            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Settings.route)
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                when {
                                    currentRoute?.startsWith("pokemon_list") == true -> {
                                        val gen = navBackStackEntry?.arguments?.getInt("gen") ?: 1
                                        Generations.find { it.id == gen }?.name ?: "Pokédex"
                                    }
                                    currentRoute == Screen.Settings.route -> "Settings"
                                    currentRoute?.startsWith("pokemon_detail") == true -> "Details"
                                    else -> "Pokédex"
                                }
                            )
                        },
                        navigationIcon = {
                            if (canNavigateBack) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.PokemonList.createRoute(1),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(
                        route = Screen.PokemonList.route,
                        arguments = listOf(navArgument("gen") {
                            type = NavType.IntType
                            defaultValue = 1
                        })
                    ) { backStackEntry ->
                        val gen = backStackEntry.arguments?.getInt("gen") ?: 1
                        val genInfo = Generations.find { it.id == gen } ?: Generations[0]
                        val viewModel: PokemonViewModel = hiltViewModel()

                        LaunchedEffect(key1 = gen) {
                            viewModel.loadPokemonByGeneration(genInfo.startId, genInfo.endId)
                        }

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
                    composable(Screen.Settings.route) {
                        SettingsScreen(viewModel = settingsViewModel)
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
}

@Composable
fun PokeAPIMainScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var query by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        PokemonGrid(
            pokemonList = pokemonList,
            query = query,
            onQueryChange = { query = it },
            onPokemonClick = onPokemonClick
        )
        LoadingOverlay(isLoading = isLoading)
    }
}

@Composable
fun PokemonGrid(
    pokemonList: List<PokemonListItem>,
    query: String,
    onQueryChange: (String) -> Unit,
    onPokemonClick: (String) -> Unit
) {
    val filtered = pokemonList.filter { it.name.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Search bar ────────────────────────────────────────────────────────
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            placeholder = { Text("Search Pokémon…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(50),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // ── Grid ──────────────────────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered) { pokemon ->
                PokemonItem(item = pokemon, onItemClick = onPokemonClick)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 720)
@Composable
fun PokemonGridPreview() {
    PokeApiTestTheme {
        PokemonGrid(
            pokemonList = listOf(
                PokemonListItem(id = 1, name = "bulbasaur", primaryType = PokemonType.GRASS),
                PokemonListItem(id = 4, name = "charmander", primaryType = PokemonType.FIRE),
                PokemonListItem(id = 7, name = "squirtle", primaryType = PokemonType.WATER),
                PokemonListItem(id = 25, name = "pikachu", primaryType = PokemonType.ELECTRIC),
            ),
            query = "",
            onQueryChange = {},
            onPokemonClick = {}
        )
    }
}
