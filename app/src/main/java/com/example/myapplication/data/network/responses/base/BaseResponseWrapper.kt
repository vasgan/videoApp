package com.example.myapplication.data.network.responses.base

import androidx.annotation.Keep
import retrofit2.Response

typealias BaseResponseWrapper<T> = Response<BaseResponse<T>>


@Keep
data class BaseResponse<T>(
    val data: T,
    val errorMessage: String,
    val metadata: String
)
