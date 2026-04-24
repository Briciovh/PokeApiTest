package com.example.pokeapitest.data.repository

import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.data.local.entity.PokemonMoveEntity
import com.example.pokeapitest.data.local.entity.PokemonVarietyEntity
import com.example.pokeapitest.data.local.entity.PokemonWithDetails
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
    fun pokemonDto_toEntityData_mapsMoves_correctly() {
        val dto = buildPokemonDto(id = 25, name = "pikachu")
        val species = buildSpeciesDto(id = 25, name = "pikachu", varietyUrl = "https://pokeapi.co/api/v2/pokemon/25/")
        val moveDetails = listOf(
            MoveDetailDto(id = 1, name = "tackle", power = 40,   type = MoveTypeDto("normal")),
            MoveDetailDto(id = 2, name = "growl",  power = null, type = MoveTypeDto("normal"))
        )

        val entityData = dto.toEntityData(species, moveDetails)

        assertThat(entityData.moves).hasSize(2)
        assertThat(entityData.moves[0].name).isEqualTo("tackle")
        assertThat(entityData.moves[0].power).isEqualTo(40)
        assertThat(entityData.moves[1].name).isEqualTo("growl")
        assertThat(entityData.moves[1].power).isEqualTo(0)
    }

    @Test
    fun pokemonDto_toEntityData_mapsVarieties_correctly() {
        val dto = buildPokemonDto(id = 1, name = "bulbasaur")
        val species = buildSpeciesDto(id = 1, name = "bulbasaur", varietyUrl = "https://pokeapi.co/api/v2/pokemon/1/")

        val entityData = dto.toEntityData(species, emptyList())

        val expectedPixel   = pokemonPixelArtUrl(1)
        val expectedArtwork = pokemonOfficialArtworkUrl(1)
        assertThat(entityData.varieties).hasSize(1)
        assertThat(entityData.varieties[0].name).isEqualTo("bulbasaur")
        assertThat(entityData.varieties[0].url).isEqualTo("https://pokeapi.co/api/v2/pokemon/1/")
        assertThat(entityData.varieties[0].isDefault).isTrue()
        assertThat(entityData.varieties[0].imageUrl).isEqualTo(expectedPixel)
        assertThat(entityData.varieties[0].officialArtworkUrl).isEqualTo(expectedArtwork)
    }

    // endregion

    // region PokemonWithDetails.toDomain

    @Test
    fun pokemonWithDetails_toDomain_mapsVarieties_correctly() {
        val pokemon = buildPokemonEntity(id = 1, name = "bulbasaur")
        val varieties = listOf(
            PokemonVarietyEntity(
                pokemonId = 1,
                name = "bulbasaur",
                url = "https://pokeapi.co/api/v2/pokemon/1/",
                isDefault = true,
                imageUrl = pokemonPixelArtUrl(1),
                officialArtworkUrl = pokemonOfficialArtworkUrl(1)
            )
        )
        val withDetails = PokemonWithDetails(pokemon, varieties, emptyList())

        val domain = withDetails.toDomain()

        val original = domain.varieties.first { !it.isShiny }
        assertThat(original.name).isEqualTo("bulbasaur")
        assertThat(original.url).isEqualTo("https://pokeapi.co/api/v2/pokemon/1/")
        assertThat(original.isDefault).isTrue()
        assertThat(original.imageUrl).isEqualTo(pokemonPixelArtUrl(1))
        assertThat(original.officialArtworkUrl).isEqualTo(pokemonOfficialArtworkUrl(1))
    }

    @Test
    fun pokemonWithDetails_toDomain_injectsShinyVariety_afterDefault() {
        val pokemon = buildPokemonEntity(id = 1, name = "bulbasaur")
        val varieties = listOf(
            PokemonVarietyEntity(1, "bulbasaur", "url/1/", true, "pixel1", "artwork1"),
            PokemonVarietyEntity(1, "bulbasaur-mega", "url/10001/", false, "pixel10001", "artwork10001")
        )
        val withDetails = PokemonWithDetails(pokemon, varieties, emptyList())

        val domain = withDetails.toDomain()

        // Expected order: [default, shiny, non-default]
        assertThat(domain.varieties).hasSize(3)
        assertThat(domain.varieties[0].name).isEqualTo("bulbasaur")
        assertThat(domain.varieties[0].isDefault).isTrue()
        assertThat(domain.varieties[1].isShiny).isTrue()
        assertThat(domain.varieties[1].name).isEqualTo("bulbasaur-shiny")
        assertThat(domain.varieties[2].name).isEqualTo("bulbasaur-mega")
    }

    @Test
    fun pokemonWithDetails_toDomain_injectsShinyVariety_atEnd_whenNoDefault() {
        val pokemon = buildPokemonEntity(id = 25, name = "pikachu")
        val varieties = listOf(
            PokemonVarietyEntity(25, "pikachu", "url/25/", false, "pixel25", "artwork25")
        )
        val withDetails = PokemonWithDetails(pokemon, varieties, emptyList())

        val domain = withDetails.toDomain()

        assertThat(domain.varieties).hasSize(2)
        assertThat(domain.varieties[0].isShiny).isFalse()
        assertThat(domain.varieties[1].isShiny).isTrue()
    }

    @Test
    fun pokemonWithDetails_toDomain_sortsMoves_byPowerDesc_thenNameAsc() {
        val pokemon = buildPokemonEntity(id = 1, name = "charmander")
        val moves = listOf(
            PokemonMoveEntity(1, "zzz-move", 50, PokemonType.FIRE),
            PokemonMoveEntity(1, "aaa-move", 50, PokemonType.WATER),
            PokemonMoveEntity(1, "flamethrower", 90, PokemonType.FIRE),
            PokemonMoveEntity(1, "scratch", 40, PokemonType.NORMAL)
        )
        val withDetails = PokemonWithDetails(pokemon, emptyList(), moves)

        val domain = withDetails.toDomain()

        assertThat(domain.moves.map { it.name })
            .containsExactly("flamethrower", "aaa-move", "zzz-move", "scratch")
            .inOrder()
    }

    @Test
    fun pokemonWithDetails_toDomain_handlesEmptyMoves() {
        val pokemon = buildPokemonEntity(id = 1, name = "bulbasaur")
        val withDetails = PokemonWithDetails(pokemon, emptyList(), emptyList())

        val domain = withDetails.toDomain()

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
        name: String
    ) = PokemonEntity(
        id = id,
        name = name,
        height = 4,
        weight = 60,
        frontDefault = null,
        types = emptyList()
    )

    // endregion
}
