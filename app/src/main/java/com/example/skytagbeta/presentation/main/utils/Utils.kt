package com.example.skytagbeta.presentation.main.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import java.text.SimpleDateFormat
import java.util.*


fun getBatteryPercentage(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { iFilter ->
        context.registerReceiver(null, iFilter)
    }
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

    val batteryPct = level / scale.toFloat()

    return (batteryPct * 100).toInt()
}


fun getGpsStatus(ctx: Context): Boolean {
    val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun networkStatus(ctx: Context): Boolean {
    val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val actNetInfo = connectivityManager!!.activeNetworkInfo
    return actNetInfo != null && actNetInfo.isConnected
}

fun bluetoothStatus(ctx: Context): Boolean {
    val bluetoothManager: BluetoothManager = ctx.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    return bluetoothAdapter!!.isEnabled
}

fun getDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}


fun vibratePhone(ctx: Context) {
    val vibrator = ctx?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
}

