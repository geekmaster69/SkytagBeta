package com.example.skytagbeta.presentation.main.service.network

import com.example.skytagbeta.base.Constants
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface UserApiClient {
    @POST(Constants.TAG_KEY_LOCATIO_PATH)
    suspend fun sendInfoSos(@Body data: UserInfo) : UserInfoResponse
}