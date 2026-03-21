package com.example.pokeapitest.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilsTest {

    @Test
    fun `capitalizeWords transforms all words to title case`() {
        val input = "pikachu bulbasaur charmander"
        val expected = "Pikachu Bulbasaur Charmander"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun `capitalizeWords handles already uppercase strings`() {
        val input = "PIKACHU"
        val expected = "Pikachu"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun `capitalizeWords handles mixed case strings`() {
        val input = "pIkAcHu"
        val expected = "Pikachu"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun `capitalizeWords handles empty string`() {
        val input = ""
        val expected = ""
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }
}
