package com.example.pokeapitest.data.remote

import com.example.pokeapitest.data.remote.dto.PokemonListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 151
    ): PokemonListDto

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}
