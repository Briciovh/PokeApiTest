package com.example.pokeapitest.domain.use_case

import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(name: String): PokemonDto {
        return repository.getPokemonDetail(name)
    }
}
