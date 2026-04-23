package com.example.pokeapitest.domain.use_case

import com.example.pokeapitest.data.repository.PokemonRepository
import com.example.pokeapitest.data.repository.toDomain
import com.example.pokeapitest.domain.model.PokemonListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(startId: Int, endId: Int): Flow<List<PokemonListItem>> {
        return repository.getPokemonList(startId, endId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
