package com.example.pokeapitest.util

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.google.common.truth.Truth.assertThat
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorUtilsTest {

    @Test
    fun httpException_404_returnsNotFoundMessage() {
        val ex = HttpException(Response.error<Any>(404, "".toResponseBody()))
        assertThat(ex.toUserMessage()).isEqualTo("Pokemon not found")
    }

    @Test
    fun httpException_500_returnsServerErrorMessage() {
        val ex = HttpException(Response.error<Any>(500, "".toResponseBody()))
        assertThat(ex.toUserMessage()).isEqualTo("Server error, please try again")
    }

    @Test
    fun httpException_otherCode_returnsCodeInMessage() {
        val ex = HttpException(Response.error<Any>(403, "".toResponseBody()))
        assertThat(ex.toUserMessage()).isEqualTo("Request failed (HTTP 403)")
    }

    @Test
    fun ioException_returnsNoInternetMessage() {
        val ex = IOException("timeout")
        assertThat((ex as Exception).toUserMessage()).isEqualTo("No internet connection")
    }

    @Test
    fun jsonDataException_returnsUnexpectedDataMessage() {
        val ex = JsonDataException("missing field")
        assertThat(ex.toUserMessage()).isEqualTo("Unexpected data from server")
    }

    @Test
    fun jsonEncodingException_returnsUnexpectedDataMessage() {
        val ex = JsonEncodingException("malformed json")
        assertThat((ex as Exception).toUserMessage()).isEqualTo("Unexpected data from server")
    }

    @Test
    fun unknownException_returnsSomethingWentWrongMessage() {
        val ex = RuntimeException("whoops")
        assertThat((ex as Exception).toUserMessage()).isEqualTo("Something went wrong")
    }
}
