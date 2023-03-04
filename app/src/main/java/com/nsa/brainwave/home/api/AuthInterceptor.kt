package com.nsa.brainwave.home.api

import android.content.Context
import com.nsa.brainwave.R
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor  : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Authorization","Bearer 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")
        return chain.proceed(requestBuilder.build())
    }
}