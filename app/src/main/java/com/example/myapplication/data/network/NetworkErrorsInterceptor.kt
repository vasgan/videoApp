package com.example.myapplication.data.network

import okhttp3.Interceptor
import okhttp3.Response

class NetworkErrorsInterceptor : Interceptor {
    // todo сделать обработку ошибок 400, 401, 403, 404, 422 NetworkException, TimeOutException
    override fun intercept(chain: Interceptor.Chain): Response {
        val currRequest = chain.request()
        val response = chain.proceed(currRequest)
        return response
    }
}