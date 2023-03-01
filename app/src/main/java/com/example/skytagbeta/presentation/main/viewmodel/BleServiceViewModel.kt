package com.example.skytagbeta.presentation.main.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytagbeta.base.db.StatusListApplication
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity
import com.example.skytagbeta.presentation.main.service.BleService
import kotlinx.coroutines.launch

class BleServiceViewModel: ViewModel() {
    private val TAG = "BleServiceViewModel"

    private var mBinder = MutableLiveData<BleService.MyBinder?>()
    var statusInfo = MutableLiveData<MutableList<StatusListEntity>>()



    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: connected to service")
            val binder = service as BleService.MyBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBinder.postValue(null)
        }
    }

    fun getBinder(): LiveData<BleService.MyBinder?> {
        return mBinder
    }

    fun getServiceConnection() : ServiceConnection{
        return serviceConnection
    }

    fun getStatusList(){
        viewModelScope.launch {
            val statusList = StatusListApplication.database.statusDao().getStatusList()
            statusInfo.postValue(statusList)
        }
    }

    fun deleteAllStatus(){
        viewModelScope.launch {
            val statusList = StatusListApplication.database.statusDao().deleteAllStatus()
        }
    }






}