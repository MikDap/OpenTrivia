package com.example.opentrivia.api

data class OpenTriviaResponse (
    val response_code: Int,
    val results: List<Domanda>)
