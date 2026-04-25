package com.example.pokeapitest.util

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.io.IOException

fun Exception.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        404         -> "Pokemon not found"
        in 500..599 -> "Server error, please try again"
        else        -> "Request failed (HTTP ${code()})"
    }
    // JsonEncodingException extends IOException, so must be checked first
    is JsonDataException,
    is JsonEncodingException -> "Unexpected data from server"
    is IOException           -> "No internet connection"
    else                     -> "Something went wrong"
}
