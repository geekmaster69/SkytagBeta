package com.example.skytagbeta.presentation.main.service.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.model.UserInfoResponse
import com.example.skytagbeta.presentation.main.service.network.UserService
import kotlinx.coroutines.launch

private const val TAG = "Service ViewMOdel"
class ServiceViewModel : ViewModel(){
    var gpsModel = MutableLiveData<UserInfoResponse>()
    private val updateServiceLocation = UserService()

    fun gpsLocationServer(userInfo: UserInfo){
        viewModelScope.launch {

            try {
                val result = updateServiceLocation.updateUserInfo(userInfo)
                gpsModel.postValue(result)
                Log.d(TAG, "$result")

            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }

        }
    }


}