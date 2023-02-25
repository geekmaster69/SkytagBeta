package com.example.skytagbeta.presentation.login.network


import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytagbeta.base.BaseRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUserService {
    private val retrofit = BaseRetrofit.getRetrofit()

    suspend fun login(loginUserInfo: LoginUserInfo) : LoginResponse {
        return withContext(Dispatchers.IO){
            val response = retrofit.create(LoginApiClient::class.java).onLogin(loginUserInfo)
            response
        }
    }
}