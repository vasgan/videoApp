package com.example.myapplication.data.network.request

data class SearchRequest(
    val page: Int,
    val limit: Int,
    val f: RequestFilter
)