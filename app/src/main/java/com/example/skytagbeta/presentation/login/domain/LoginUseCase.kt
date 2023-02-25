package com.example.skytagbeta.presentation.login.domain


import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytagbeta.presentation.login.network.LoginUserService

class LoginUseCase {

    private val loginUserService = LoginUserService()

    suspend operator fun invoke(loginUserInfo: LoginUserInfo): LoginResponse {

        return loginUserService.login(loginUserInfo)
    }


}