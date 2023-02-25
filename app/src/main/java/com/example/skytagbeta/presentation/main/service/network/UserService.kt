package com.example.skytagbeta.presentation.main.service.network

import com.example.skytagbeta.base.BaseRetrofit
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService {
    private val retrofit = BaseRetrofit.getRetrofit()

    suspend fun updateUserInfo(userInfo: UserInfo) : UserInfoResponse {
        return withContext(Dispatchers.IO){
            val response = retrofit.create(UserApiClient::class.java).sendInfoSos(userInfo)
            response
        }
    }
}