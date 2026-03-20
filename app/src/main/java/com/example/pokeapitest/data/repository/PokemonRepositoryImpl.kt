package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonListDto
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi
) : PokemonRepository {
    override suspend fun getPokemonList(limit: Int): PokemonListDto {
        return api.getPokemonList(limit)
    }

    override suspend fun getPokemonDetail(name: String): PokemonDto {
        return api.getPokemonDetail(name)
    }
}

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int): PokemonListDto
    suspend fun getPokemonDetail(name: String): PokemonDto
}
