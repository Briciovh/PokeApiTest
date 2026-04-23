package com.example.pokeapitest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapitest.domain.model.PokemonListItem
import com.example.pokeapitest.domain.use_case.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<PokemonListItem>>(emptyList())
    val pokemonList: StateFlow<List<PokemonListItem>> = _pokemonList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorChannel = MutableSharedFlow<String>(replay = 1)
    val errorChannel: SharedFlow<String> = _errorChannel.asSharedFlow()

    fun loadPokemonByGeneration(startId: Int, endId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getPokemonListUseCase(startId, endId).collectLatest { items ->
                    _pokemonList.value = items
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorChannel.emit("Failed to load Pokemon list: ${e.message}")
                _isLoading.value = false
            }
        }
    }
}
