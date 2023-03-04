package com.nsa.brainwave.home.repository

import com.nsa.brainwave.home.api.ApiClient
import com.nsa.brainwave.home.api.ApiResult
import com.nsa.brainwave.home.api.ApiUtil
import com.nsa.brainwave.home.models.Api.RequestModel
import com.nsa.brainwave.home.models.Api.ResponseModel

class Repository {

    suspend fun getCompleteionText(requestModel: RequestModel): ApiResult<ResponseModel> {
        return ApiUtil.getResponse(
            request = { ApiClient.getService().getCompleteionText(requestModel) },
            "Something went wrong.",
            0
        )
    }

}