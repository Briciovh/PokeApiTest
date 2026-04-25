package com.example.pokeapitest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapitest.domain.model.PokemonDetail
import com.example.pokeapitest.domain.use_case.GetPokemonDetailUseCase
import com.example.pokeapitest.util.toUserMessage
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
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<PokemonDetail?>(null)
    val pokemonDetail: StateFlow<PokemonDetail?> = _pokemonDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorChannel = MutableSharedFlow<String>(replay = 1)
    val errorChannel: SharedFlow<String> = _errorChannel.asSharedFlow()

    private var lastPokemonName = ""

    fun loadPokemonDetail(name: String) {
        lastPokemonName = name
        doLoad(name)
    }

    fun retry() {
        if (lastPokemonName.isNotEmpty()) doLoad(lastPokemonName)
    }

    private fun doLoad(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getPokemonDetailUseCase(name.lowercase()).collectLatest { detail ->
                    _pokemonDetail.value = detail
                    if (detail != null) {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _errorChannel.emit("Could not load $name: ${e.toUserMessage()}")
                _isLoading.value = false
            }
        }
    }
}
