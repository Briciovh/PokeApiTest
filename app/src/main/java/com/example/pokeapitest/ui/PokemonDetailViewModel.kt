package com.example.pokeapitest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.domain.use_case.GetPokemonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<PokemonDto?>(null)
    val pokemonDetail: StateFlow<PokemonDto?> = _pokemonDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorChannel = MutableSharedFlow<String>()
    val errorChannel: SharedFlow<String> = _errorChannel.asSharedFlow()

    fun loadPokemonDetail(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pokemonDetail.value = getPokemonDetailUseCase(name.lowercase())
            } catch (e: Exception) {
                _errorChannel.emit("Failed to load Pokemon detail: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
