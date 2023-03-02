package com.example.skytagbeta.presentation.main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.ActivityMainBinding
import com.example.skytagbeta.presentation.login.LoginActivity
import com.example.skytagbeta.presentation.main.service.BleService
import com.example.skytagbeta.presentation.main.utils.bluetoothStatus
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.main.worker.BlurViewModelFactory
import com.example.skytagbeta.presentation.main.worker.WorkerViewModel
import com.example.skytagbeta.presentation.locationhistory.LocationHistory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.paperdb.Paper
private const val TAG = "MainActivity"
@SuppressLint("MissingPermission")
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

        binding.btnAddBluetooth.setOnClickListener { mService.scanDevice() }


        binding.btnStar.setOnClickListener {
            val isActive = Paper.book().read<Boolean>("isActiveWorker") ?: false

                 if (isActive){
                     mWorkerViewModel.cancelWork()
                     binding.btnStar.text = "Transmitir"
                     binding.btnStar.setBackgroundColor(Color.GREEN)
                     Paper.book().write("isActiveWorker", false)

                 }else{
                     mWorkerViewModel.updateLocation()
                     binding.btnStar.text = "Detener"
                     binding.btnStar.setBackgroundColor(Color.RED)
                     Paper.book().write("isActiveWorker", true)

                 }

            }

    }

    override fun onResume() {
        super.onResume()
        starBleService()
        checkDeviceLocationSettingsAndStartGeofence()
        if (!bluetoothStatus(this)) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
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
            priority = LocationRequest.PRIORITY_LOW_POWER }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try { exception.startResolutionForResult(this, 23)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.action_record ->{
                startActivity(Intent(this, LocationHistory::class.java))
            }
            R.id.action_exit ->{
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        Paper.book().write("active", false)
        mWorkerViewModel.cancelWork()
        unbindService(mViewModel.getServiceConnection())
        val  serviceIntent = Intent(this, BleService::class.java)
        stopService(serviceIntent)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}