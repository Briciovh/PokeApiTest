package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi
) : PokemonRepository {
    override suspend fun getPokemonList(limit: Int): PokemonListDto {
        return api.getPokemonList(limit)
    }
}

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int): PokemonListDto
}
