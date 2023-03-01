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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.databinding.ActivityMainBinding
import com.example.skytagbeta.presentation.login.LoginActivity
import com.example.skytagbeta.presentation.main.adapter.StatusListAdapter
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity
import com.example.skytagbeta.presentation.main.service.BleService
import com.example.skytagbeta.presentation.main.utils.bluetoothStatus
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.main.worker.BlurViewModelFactory
import com.example.skytagbeta.presentation.main.worker.WorkerViewModel
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
    private lateinit var mAdapter: StatusListAdapter
    private lateinit var mLinearLayout: LinearLayoutManager
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

        binding.btnLogout.setOnClickListener { logOut() }

        binding.btnAddBluetooth.setOnClickListener {
            if (!bluetoothStatus(this)) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            }else{ mService.scanDevice() }
        }

        binding.btnStop.setOnClickListener { mWorkerViewModel.cancelWork() }

        binding.btnStar.setOnClickListener { mWorkerViewModel.updateLocation() }

        binding.btnRefresh.setOnClickListener { setupRecyclerView() }

        binding.btnDeleteAll.setOnClickListener{mViewModel.deleteAllStatus()}
    }

    private fun setupRecyclerView() {

        mViewModel.getStatusList()
        mViewModel.statusInfo.observe(this){statusList ->
            if (statusList.isEmpty()){
                mAdapter = StatusListAdapter(mutableListOf())
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }
            }else{
                mAdapter = StatusListAdapter(statusList.reversed() as MutableList<StatusListEntity>)
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }

            }


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
}