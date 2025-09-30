package com.example.myapplication.data.network.request

data class RequestFilter(
    val genres: List<Int>,
    val types: List<String>,
    val seasons: List<String>,
    val years: RequestYears?,
    val search: String,
    val sorting: String,
    val age_ratings: List<String>,
    val publish_statuses: List<String>,
    val production_statuses: List<String>
)