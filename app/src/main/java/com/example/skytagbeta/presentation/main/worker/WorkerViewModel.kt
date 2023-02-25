package com.example.skytagbeta.presentation.main.worker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkerViewModel(application: Application): ViewModel() {

    private val workManager = WorkManager.getInstance(application)


    fun cancelWork(){
        workManager.cancelAllWork()
    }

    fun updateLocation(){
        val constrains = Constraints.Builder()
            .build()

        val updateLocation = PeriodicWorkRequest.Builder(
            UpdateLocationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constrains)
            .addTag("updateLocation")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "updateLocation",
            ExistingPeriodicWorkPolicy.KEEP,
            updateLocation )
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
