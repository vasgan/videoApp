package com.example.myapplication.data.network.responses

data class NameResponse(
    val main: String,
    val english: String,
    val alternative: String? = null
)