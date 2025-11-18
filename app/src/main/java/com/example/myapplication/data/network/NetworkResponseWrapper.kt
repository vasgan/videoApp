package com.example.myapplication.data.network

sealed class NetworkResponseWrapper<out T>

data class Success<T>(val data: T) : NetworkResponseWrapper<T>()
data class Error<T>(val error: String, val cached: T? = null) : NetworkResponseWrapper<T>()
class Loading<T> : NetworkResponseWrapper<T>()
