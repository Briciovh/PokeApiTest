package com.example.pokeapitest.domain.use_case

import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(name: String): Flow<PokemonDto?> {
        return repository.getPokemonDetail(name)
    }
}
