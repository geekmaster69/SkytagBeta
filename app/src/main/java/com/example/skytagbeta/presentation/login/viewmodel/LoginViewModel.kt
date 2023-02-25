package com.example.skytagbeta.presentation.login.viewmodel

import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytagbeta.presentation.login.domain.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    val loginUseCase = LoginUseCase()
    var loginModel = MutableLiveData<LoginResponse>()


    fun onLogin(loginUserInfo: LoginUserInfo){
        viewModelScope.launch {
            try {
                val result = loginUseCase(loginUserInfo)
                loginModel.postValue(result)
                Log.e("ViewModelResult", result.toString())

            } catch (e: Exception) {

            }
        }
    }
}