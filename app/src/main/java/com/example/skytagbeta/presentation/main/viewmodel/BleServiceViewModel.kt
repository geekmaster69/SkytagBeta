package com.example.skytagbeta.presentation.main.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skytagbeta.presentation.main.service.BleService

const val  TAG = "BleServiceViewModel"
class BleServiceViewModel: ViewModel() {

    private var mBinder = MutableLiveData<BleService.MyBinder?>()

    var connectionState = MutableLiveData<Boolean>()

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

    fun getConnectionState(){
    }


}