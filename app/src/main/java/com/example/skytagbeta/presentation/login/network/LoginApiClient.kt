package com.example.skytagbeta.presentation.login.network

import com.example.skytagbeta.presentation.login.model.LoginResponse
import com.example.skytagbeta.presentation.login.model.LoginUserInfo
import com.example.skytagbeta.base.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiClient {
    @POST(Constants.TAG_KEY_LOGIN_PATH)
    suspend fun onLogin(@Body data: LoginUserInfo) : LoginResponse
}
