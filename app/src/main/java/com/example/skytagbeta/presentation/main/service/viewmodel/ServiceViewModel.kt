package com.example.skytagbeta.presentation.main.service.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.model.UserInfoResponse
import com.example.skytagbeta.presentation.main.service.network.UserService
import kotlinx.coroutines.launch

class ServiceViewModel : ViewModel(){
    var gpsModel = MutableLiveData<UserInfoResponse>()
    val updateServiceLocation = UserService()

    fun gpsLocationServer(userInfo: UserInfo){
        viewModelScope.launch {

            val result = updateServiceLocation.updateUserInfo(userInfo)

            Log.w("RESPUESTA ViewModel", result.toString())

            gpsModel.postValue(result)


        }
    }
}