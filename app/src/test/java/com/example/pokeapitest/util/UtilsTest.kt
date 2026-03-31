package com.example.pokeapitest.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilsTest {

    @Test
    fun capitalizeWords_transformsAllWordsToTitleCase() {
        val input = "pikachu bulbasaur charmander"
        val expected = "Pikachu Bulbasaur Charmander"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun capitalizeWords_handlesAlreadyUppercaseStrings() {
        val input = "PIKACHU"
        val expected = "Pikachu"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun capitalizeWords_handlesMixedCaseStrings() {
        val input = "pIkAcHu"
        val expected = "Pikachu"
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }

    @Test
    fun capitalizeWords_handlesEmptyString() {
        val input = ""
        val expected = ""
        
        assertThat(input.capitalizeWords()).isEqualTo(expected)
    }
}
