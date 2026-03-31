package com.example.pokeapitest.data.local

import androidx.room.TypeConverter
import com.example.pokeapitest.domain.model.PokemonType

class Converters {
    @TypeConverter
    fun fromPokemonTypeList(types: List<PokemonType>): String {
        return types.joinToString(",") { it.typeName }
    }

    @TypeConverter
    fun toPokemonTypeList(typesString: String): List<PokemonType> {
        return typesString.split(",").filter { it.isNotEmpty() }.map {
            PokemonType.fromString(it)
        }
    }
}
