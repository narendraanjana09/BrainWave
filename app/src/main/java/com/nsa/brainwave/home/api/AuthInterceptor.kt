package com.nsa.brainwave.home.api

import android.content.Context
import com.nsa.brainwave.R
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor  : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Authorization","Bearer Api_Key")
        return chain.proceed(requestBuilder.build())
    }
}
