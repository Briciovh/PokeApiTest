package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.remote.dto.MoveDetailDto
import com.example.pokeapitest.data.remote.dto.MoveDto
import com.example.pokeapitest.data.remote.dto.MoveSlotDto
import com.example.pokeapitest.data.remote.dto.MoveTypeDto
import com.example.pokeapitest.data.remote.dto.PokemonDto
import com.example.pokeapitest.data.remote.dto.PokemonResourceDto
import com.example.pokeapitest.data.remote.dto.PokemonSpeciesDto
import com.example.pokeapitest.data.remote.dto.PokemonVarietyDto
import com.example.pokeapitest.data.remote.dto.SpritesDto
import com.example.pokeapitest.domain.model.PokemonType
import com.example.pokeapitest.util.pokemonOfficialArtworkUrl
import com.example.pokeapitest.util.pokemonPixelArtUrl
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MapperTest {

    // region PokemonDto.toEntity

    @Test
    fun pokemonDto_toEntity_serializesMoves_correctly() {
        val dto = buildPokemonDto(id = 25, name = "pikachu")
        val species = buildSpeciesDto(id = 25, name = "pikachu", varietyUrl = "https://pokeapi.co/api/v2/pokemon/25/")
        val moveDetails = listOf(
            MoveDetailDto(id = 1, name = "tackle", power = 40,   type = MoveTypeDto("normal")),
            MoveDetailDto(id = 2, name = "growl",  power = null, type = MoveTypeDto("normal"))
        )

        val entity = dto.toEntity(species, moveDetails)

        assertThat(entity.moves).isEqualTo("tackle|40|normal;growl|0|normal")
    }

    @Test
    fun pokemonDto_toEntity_handlesNullMovePower_asZero() {
        val dto = buildPokemonDto(id = 1, name = "charmander")
        val species = buildSpeciesDto(id = 1, name = "charmander", varietyUrl = "https://pokeapi.co/api/v2/pokemon/1/")
        val moveDetails = listOf(
            MoveDetailDto(id = 10, name = "growl", power = null, type = MoveTypeDto("normal"))
        )

        val entity = dto.toEntity(species, moveDetails)

        assertThat(entity.moves).isEqualTo("growl|0|normal")
    }

    @Test
    fun pokemonDto_toEntity_serializesVarieties_correctly() {
        val dto = buildPokemonDto(id = 1, name = "bulbasaur")
        val species = buildSpeciesDto(id = 1, name = "bulbasaur", varietyUrl = "https://pokeapi.co/api/v2/pokemon/1/")

        val entity = dto.toEntity(species, emptyList())

        val expectedPixel   = pokemonPixelArtUrl(1)
        val expectedArtwork = pokemonOfficialArtworkUrl(1)
        assertThat(entity.varieties)
            .isEqualTo("bulbasaur|https://pokeapi.co/api/v2/pokemon/1/|true|$expectedPixel|$expectedArtwork")
    }

    // endregion

    // region PokemonEntity.toDomain

    @Test
    fun pokemonEntity_toDomain_parsesVarieties_correctly() {
        val entity = buildPokemonEntity(
            id = 1, name = "bulbasaur",
            varieties = "bulbasaur|https://pokeapi.co/api/v2/pokemon/1/|true" +
                "|${pokemonPixelArtUrl(1)}|${pokemonOfficialArtworkUrl(1)}"
        )

        val domain = entity.toDomain()

        val original = domain.varieties.first { !it.isShiny }
        assertThat(original.name).isEqualTo("bulbasaur")
        assertThat(original.url).isEqualTo("https://pokeapi.co/api/v2/pokemon/1/")
        assertThat(original.isDefault).isTrue()
        assertThat(original.imageUrl).isEqualTo(pokemonPixelArtUrl(1))
        assertThat(original.officialArtworkUrl).isEqualTo(pokemonOfficialArtworkUrl(1))
    }

    @Test
    fun pokemonEntity_toDomain_injectsShinyVariety_afterDefault() {
        val entity = buildPokemonEntity(
            id = 1, name = "bulbasaur",
            varieties = "bulbasaur|url/1/|true|pixel1|artwork1;" +
                        "bulbasaur-mega|url/10001/|false|pixel10001|artwork10001"
        )

        val domain = entity.toDomain()

        // Expected order: [default, shiny, non-default]
        assertThat(domain.varieties).hasSize(3)
        assertThat(domain.varieties[0].name).isEqualTo("bulbasaur")
        assertThat(domain.varieties[0].isDefault).isTrue()
        assertThat(domain.varieties[1].isShiny).isTrue()
        assertThat(domain.varieties[1].name).isEqualTo("bulbasaur-shiny")
        assertThat(domain.varieties[2].name).isEqualTo("bulbasaur-mega")
    }

    @Test
    fun pokemonEntity_toDomain_injectsShinyVariety_atEnd_whenNoDefault() {
        val entity = buildPokemonEntity(
            id = 25, name = "pikachu",
            varieties = "pikachu|url/25/|false|pixel25|artwork25"
        )

        val domain = entity.toDomain()

        assertThat(domain.varieties).hasSize(2)
        assertThat(domain.varieties[0].isShiny).isFalse()
        assertThat(domain.varieties[1].isShiny).isTrue()
    }

    @Test
    fun pokemonEntity_toDomain_sortsMoves_byPowerDesc_thenNameAsc() {
        val entity = buildPokemonEntity(
            id = 1, name = "charmander",
            moves = "zzz-move|50|fire;aaa-move|50|water;flamethrower|90|fire;scratch|40|normal"
        )

        val domain = entity.toDomain()

        assertThat(domain.moves.map { it.name })
            .containsExactly("flamethrower", "aaa-move", "zzz-move", "scratch")
            .inOrder()
    }

    @Test
    fun pokemonEntity_toDomain_parsesEmptyMoves_toEmptyList() {
        val entity = buildPokemonEntity(id = 1, name = "bulbasaur", moves = "")

        val domain = entity.toDomain()

        assertThat(domain.moves).isEmpty()
    }

    // endregion

    // region PokemonListItemEntity.toDomain

    @Test
    fun pokemonListItemEntity_toDomain_mapsFieldsCorrectly() {
        val entity = PokemonListItemEntity(
            name = "pikachu",
            url = "https://pokeapi.co/api/v2/pokemon/25/",
            id = 25,
            primaryType = PokemonType.ELECTRIC
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo(25)
        assertThat(domain.name).isEqualTo("pikachu")
        assertThat(domain.primaryType).isEqualTo(PokemonType.ELECTRIC)
    }

    // endregion

    // region helpers

    private fun buildPokemonDto(id: Int, name: String) = PokemonDto(
        id = id,
        name = name,
        height = 4,
        weight = 60,
        sprites = SpritesDto(frontDefault = null),
        types = emptyList(),
        moves = emptyList()
    )

    private fun buildSpeciesDto(id: Int, name: String, varietyUrl: String) = PokemonSpeciesDto(
        id = id,
        name = name,
        varieties = listOf(
            PokemonVarietyDto(
                isDefault = true,
                pokemon = PokemonResourceDto(name = name, url = varietyUrl)
            )
        )
    )

    private fun buildPokemonEntity(
        id: Int,
        name: String,
        varieties: String = "",
        moves: String = ""
    ) = PokemonEntity(
        id = id,
        name = name,
        height = 4,
        weight = 60,
        frontDefault = null,
        types = emptyList(),
        varieties = varieties,
        moves = moves
    )

    // endregion
}
