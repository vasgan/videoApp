package com.example.myapplication.data.network

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkResponseWrapper<out T>

data class Success<T>(val data: T) : NetworkResponseWrapper<T>()
data class Error<T>(
    val error: String,
    val cached: T? = null,
    val networkError: NetworkError = NetworkError.fromMessage(error)
) : NetworkResponseWrapper<T>()
class Loading<T> : NetworkResponseWrapper<T>()

sealed class NetworkError {
    data object NoInternet : NetworkError()
    data object ApiUnavailable : NetworkError()
    data class Other(val message: String) : NetworkError()

    companion object {
        fun fromThrowable(throwable: Throwable): NetworkError {
            return when (throwable) {
                is UnknownHostException,
                is ConnectException,
                is SocketTimeoutException -> NoInternet
                else -> fromMessage(throwable.message.orEmpty())
            }
        }

        fun fromHttpCode(code: Int, message: String): NetworkError {
            return if (code >= 500) ApiUnavailable else Other(message)
        }

        fun fromMessage(message: String): NetworkError {
            val normalized = message.trim().lowercase()
            val httpCode = Regex("""http\s+(\d{3})""")
                .find(normalized)
                ?.groupValues
                ?.getOrNull(1)
                ?.toIntOrNull()

            return when {
                httpCode != null && httpCode >= 500 -> ApiUnavailable
                "unknownhost" in normalized ||
                        "failed to connect" in normalized ||
                        "unable to resolve host" in normalized ||
                        "timeout" in normalized -> NoInternet
                else -> Other(message)
            }
        }
    }
}
