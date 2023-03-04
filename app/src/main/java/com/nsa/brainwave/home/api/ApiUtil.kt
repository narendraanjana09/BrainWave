package com.nsa.brainwave.home.api

import android.util.Log
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import kotlin.math.log

object ApiUtil{
    @JvmStatic
    fun <T> parseError( response: Response<T>): ErrorRes.ApiError? {
        val converter: Converter<ResponseBody, ErrorRes.ApiError> = ApiClient.retrofit
            .responseBodyConverter(ErrorRes.ApiError::class.java, arrayOfNulls<Annotation>(0))
        return try {
            converter.convert(response.errorBody()!!)
        }catch (ex: Exception) {
            null
        }

    }

    suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String, errorType:Int
    ): ApiResult<T> {
        try {
            val result = request.invoke()
            if (result.isSuccessful) {
                return ApiResult.success(result.body())
            } else {
                if(errorType==0){
                    val errorResponse = parseError(result)
                    if(errorResponse is ErrorRes.ApiError){
                        return ApiResult.error(errorResponse.error?:"Something went wrong", errorResponse)
                    }
                }
            }

        } catch (e: Throwable) {
            if(e is IOException) {
                Log.e("TAG", "getResponse: $e",)
                return ApiResult.error("Please Check Your Network Connection", null)
            }else if (e is CancellationException){
                Log.d("Api Test", "Coroutine Cancelled")
            }
            Log.e("Api error",e.stackTraceToString())
        }
        return ApiResult.error(defaultErrorMessage, null)
    }
}