package com.example.pokeapitest.domain.use_case

import com.example.pokeapitest.data.repository.PokemonRepository
import com.example.pokeapitest.util.capitalizeWords
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(limit: Int = 151): Flow<List<String>> {
        return repository.getPokemonList(limit).map { list ->
            list.map { it.name.capitalizeWords() }
        }
    }
}
