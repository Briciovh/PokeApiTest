package com.example.pokeapitest.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokeapitest.data.local.entity.PokemonEntity
import com.example.pokeapitest.data.local.entity.PokemonListItemEntity
import com.example.pokeapitest.domain.model.PokemonType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonDaoTest {

    private lateinit var database: PokemonDatabase
    private lateinit var dao: PokemonDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PokemonDatabase::class.java
        ).build()
        dao = database.dao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetPokemonList() = runBlocking<Unit> {
        val list = listOf(
            PokemonListItemEntity(name = "bulbasaur", url = "url1", id = 1, primaryType = PokemonType.GRASS),
            PokemonListItemEntity(name = "ivysaur", url = "url2", id = 2, primaryType = PokemonType.GRASS)
        )
        dao.insertPokemonList(list)

        val result = dao.getPokemonList()
        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyElementsIn(list)
    }

    @Test
    fun getPokemonInRange() = runBlocking<Unit> {
        val list = (1..10).map { i ->
            PokemonListItemEntity(name = "pkm-$i", url = "url-$i", id = i)
        }
        dao.insertPokemonList(list)

        val result = dao.getPokemonInRange(3, 7)
        assertThat(result).hasSize(5)
        assertThat(result.first().id).isEqualTo(3)
        assertThat(result.last().id).isEqualTo(7)
    }

    @Test
    fun countInRange() = runBlocking<Unit> {
        val list = (1..10).map { i ->
            PokemonListItemEntity(name = "pkm-$i", url = "url-$i", id = i)
        }
        dao.insertPokemonList(list)

        val count = dao.countInRange(1, 5)
        assertThat(count).isEqualTo(5)
    }

    @Test
    fun insertAndGetPokemonDetail() = runBlocking<Unit> {
        val pokemon = PokemonEntity(
            id = 25,
            name = "pikachu",
            height = 4,
            weight = 60,
            frontDefault = "url",
            types = listOf(PokemonType.ELECTRIC),
            varieties = "var1|url|true",
            moves = "move1|power|type"
        )
        dao.insertPokemon(pokemon)

        val result = dao.getPokemonByName("pikachu")
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(25)
        assertThat(result?.name).isEqualTo("pikachu")
    }

    @Test
    fun clearPokemonList() = runBlocking<Unit> {
        val list = listOf(
            PokemonListItemEntity(name = "p1", url = "u1", id = 1)
        )
        dao.insertPokemonList(list)
        
        dao.clearPokemonList()
        
        val result = dao.getPokemonList()
        assertThat(result).isEmpty()
    }
}
