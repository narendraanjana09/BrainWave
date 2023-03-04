package com.nsa.brainwave.home.api

import com.nsa.brainwave.home.models.Api.RequestModel
import com.nsa.brainwave.home.models.Api.ResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("completions")
    suspend fun getCompleteionText(
        @Body requestBody:RequestModel
    ):Response<ResponseModel>

}