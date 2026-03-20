package com.example.pokeapitest.util

import java.util.Locale

/**
 * Extension function to capitalize the first letter of every word in a string
 * and make the rest of the letters lowercase.
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase(Locale.ROOT).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }
}
