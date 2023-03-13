package com.example.skytagbeta.presentation.locationhistory.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytagbeta.base.db.StatusListApplication
import com.example.skytagbeta.presentation.locationhistory.entity.StatusListEntity
import kotlinx.coroutines.launch

const val  TAG = "LocationViewModel"
class LocationViewModel: ViewModel() {

    var statusInfo = MutableLiveData<MutableList<StatusListEntity>>()


    fun getStatusList(){
        viewModelScope.launch {
            val statusList = StatusListApplication.database.statusDao().getStatusList()
            statusInfo.postValue(statusList)
        }
    }

    fun deleteAllStatus(){
        viewModelScope.launch {
            StatusListApplication.database.statusDao().deleteAllStatus()
        }
    }
}