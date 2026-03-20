package com.example.pokeapitest.domain.use_case

import com.example.pokeapitest.data.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(limit: Int = 151): List<String> {
        return repository.getPokemonList(limit).results.map { it.name }
    }
}
