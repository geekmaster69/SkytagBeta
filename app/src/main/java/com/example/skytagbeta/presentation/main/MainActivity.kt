package com.example.skytagbeta.presentation.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.skytagbeta.databinding.ActivityMainBinding
import com.example.skytagbeta.presentation.login.LoginActivity
import com.example.skytagbeta.presentation.main.service.BleService
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.main.worker.BlurViewModelFactory
import com.example.skytagbeta.presentation.main.worker.WorkerViewModel
import io.paperdb.Paper
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mService: BleService
    private val mViewModel: BleServiceViewModel by viewModels()
    private val mWorkerViewModel: WorkerViewModel by viewModels { BlurViewModelFactory(application) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel.getBinder().observe(this){ myBinder ->
            if (myBinder != null){
                Log.d(TAG, "onChanged: connected to service")
                mService = myBinder.getService()
            }else{
                Log.d(TAG, "onChanged: unBound from service")
            }
        }

        binding.btnLogout.setOnClickListener {
            logOut()
        }

        binding.btnAddBluetooth.setOnClickListener {
            mService.scanDevice()
        }

        binding.btnStop.setOnClickListener {
            mWorkerViewModel.cancelWork()
        }

        binding.btnStar.setOnClickListener {
            mWorkerViewModel.updateLocation()
        }
    }

    private fun logOut() {
        Paper.book().write("active", false)

        mWorkerViewModel.cancelWork()

        unbindService(mViewModel.getServiceConnection())

        val  serviceIntent = Intent(this, BleService::class.java)
        stopService(serviceIntent)

        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }

    override fun onResume() {
        super.onResume()
        starBleService()
    }

    private fun starBleService() {
        val serviceIntent = Intent(this, BleService::class.java)
        startService(serviceIntent)
        bindServices()
    }

    private fun bindServices() {
        val serviceIntent = Intent(this, BleService::class.java)
        bindService(serviceIntent, mViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE)
    }
}