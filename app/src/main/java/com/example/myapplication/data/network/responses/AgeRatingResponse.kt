package com.example.myapplication.data.network.responses

data class AgeRatingResponse(
    val value: String,
    val label: String,
    val isAdult: Boolean,
    val description: String
)