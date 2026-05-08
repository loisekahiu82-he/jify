package com.example.gigify.Network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaService {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun getAccessToken(
        @Header("Authorization") authHeader: String
    ): Response<AccessTokenResponse>

    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun sendSTKPush(
        @Header("Authorization") authHeader: String,
        @Body request: STKPushRequest
    ): Response<STKPushResponse>
}
