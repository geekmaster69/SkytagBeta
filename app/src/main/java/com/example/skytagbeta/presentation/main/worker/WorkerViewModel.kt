package com.example.skytagbeta.presentation.main.worker

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import java.util.concurrent.TimeUnit

class WorkerViewModel(application: Application): ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private var statusWorker = MutableLiveData<Boolean>()

    fun cancelWork(){
        workManager.cancelAllWork()
        statusWorker.postValue(false)
    }

    fun updateLocation(time: Long){
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()

        val updateLocation = PeriodicWorkRequest.Builder(
            UpdateLocationWorker::class.java,
            time,
            TimeUnit.MINUTES
        ).setConstraints(constrains)
            .addTag("updateLocation")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "updateLocation",
            ExistingPeriodicWorkPolicy.KEEP,
            updateLocation )
        statusWorker.postValue(true)
    }
}

class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WorkerViewModel::class.java)) {
            WorkerViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
