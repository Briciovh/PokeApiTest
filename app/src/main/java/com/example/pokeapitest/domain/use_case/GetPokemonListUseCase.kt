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
    operator fun invoke(limit: Int = 151, startId: Int? = null, endId: Int? = null): Flow<List<PokemonListItem>> {
        return repository.getPokemonList(limit).map { list ->
            val domainList = list.map { it.toDomain() }
            if (startId != null && endId != null) {
                domainList.filter { it.id in startId..endId }
            } else {
                domainList
            }
        }
    }
}
