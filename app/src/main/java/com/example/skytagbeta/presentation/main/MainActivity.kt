package com.example.skytagbeta.presentation.main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.databinding.ActivityMainBinding
import com.example.skytagbeta.presentation.login.LoginActivity
import com.example.skytagbeta.presentation.main.service.BleService
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.main.worker.BlurViewModelFactory
import com.example.skytagbeta.presentation.main.worker.WorkerViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.paperdb.Paper
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mService: BleService
    private val mViewModel: BleServiceViewModel by viewModels()
    private val mWorkerViewModel: WorkerViewModel by viewModels { BlurViewModelFactory(application) }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

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
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
                showToast(this, "Vuelva a oprimir el boton")
            }else{
                mService.scanDevice()

            }

        }

        binding.btnStop.setOnClickListener {
            mWorkerViewModel.cancelWork()
        }

        binding.btnStar.setOnClickListener {
            mWorkerViewModel.updateLocation()
        }
        binding.btnRefresh.setOnClickListener {
            val latitude = Paper.book().read<Double>("latSos")
            val longitude = Paper.book().read<Double>("longSos")
            val gpsStatus = Paper.book().read<String>("gpsStatus")
            val networkStatus = Paper.book().read<String>("networkStatus")

            binding.tvStatus.text = "$latitude,  $longitude, $gpsStatus, $networkStatus "

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
        checkDeviceLocationSettingsAndStartGeofence()
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
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(this, 23)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            }
        }
    }

}