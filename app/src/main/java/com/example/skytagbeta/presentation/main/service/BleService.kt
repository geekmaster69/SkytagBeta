package com.example.skytagbeta.presentation.main.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.widget.Toast
import com.example.skytagbeta.R
import com.example.skytagbeta.base.Constants
import com.example.skytagbeta.base.utils.makeStatusNotification
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.presentation.main.MainActivity
import com.example.skytagbeta.presentation.main.gps.DefaultLocationClient
import com.example.skytagbeta.presentation.main.gps.LocationClient
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.viewmodel.ServiceViewModel
import com.example.skytagbeta.presentation.main.utils.vibratePhone
import com.google.android.gms.location.LocationServices
import com.polidea.rxandroidble3.NotificationSetupMode
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "Service"
class BleService : Service() {
    //Enlace de service
    private val mBinder: IBinder = MyBinder()

    //Bluetooth BLE
    private lateinit var rxBleClient: RxBleClient
    private val serviceUUID: ParcelUuid = ParcelUuid.fromString(Constants.SERVICEUUID)
    private val characteristicUUID: UUID = UUID.fromString(Constants.CHARACTERISTICUUID)
    private var i: Int = 0

    //GPS Service
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient


    //Envio de informacion
    private val mGpsViewModel = ServiceViewModel()
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var date: String

    override fun onCreate() {
        super.onCreate()
        //Data Base
        Paper.init(applicationContext)
        createNotificationChannel()

        //Bluetooth
        rxBleClient = RxBleClient.create(applicationContext)

        //GPS
        locationClient = DefaultLocationClient(applicationContext, LocationServices
            .getFusedLocationProviderClient(applicationContext))

        //Fecha
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        updateLocation()
        return START_STICKY
    }

    private fun updateLocation() {
        locationClient
            .getLocationClient(1000) // La ubicacion se actualiza cada X tiempo y se guarda  en Paper
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Paper.book().write("latSos", location.latitude)
                Paper.book().write("longSos", location.longitude)
                Paper.book().write("accuracy", location.accuracy.toInt().toString())
                Paper.book().write("speed", location.speed)
                Paper.book().write("altitude", location.altitude)

                Log.e(TAG, "${location.latitude} ${location.longitude}")
            }.launchIn(serviceScope)
    }

    fun scanDevice(){
        showToast(applicationContext,"Scanned...")
        rxBleClient.scanBleDevices(scanSettings(), scanFilter())
            .firstElement()
            .subscribe({ scanResult ->
                stablesConnection(scanResult.bleDevice)
            },onError())
    }

    private fun stablesConnection(bleDevice: RxBleDevice) {
        Paper.book().write("macAddress", bleDevice.macAddress.toString())

        showToast(applicationContext,"Connected to: ${bleDevice.name!!.trim()}")
        bleDevice.establishConnection(false)
            .subscribe({ rxBleConnection ->
                rxBleConnection.setupIndication(characteristicUUID, NotificationSetupMode.COMPAT)
                    .subscribe({ observable ->
                        observable.subscribe({
                            pressButton()
                        }, onError())
                    }, onError())
            }, reconnect())
    }

    private fun pressButton() {
        val active = Paper.book().read<Boolean>("active")
        if (active!!){
            i++
            Handler(Looper.getMainLooper()).postDelayed({
                if (i==1){
                    Log.i(TAG, "Button Bluetooth Pressed!!!")
                    Handler(Looper.getMainLooper())
                        .post { makeStatusNotification(
                            "Simple Click", applicationContext, false) }
                }else if (i==2){
                    Log.i(TAG, "Button Bluetooth twice")
                    Handler(Looper.getMainLooper())
                        .post { makeStatusNotification(
                            "CLICK SOS", applicationContext, true) }
                    sendLocation()
                }
                i = 0
            }, 1000)
        }
    }

    private fun sendLocation() {

        val latitude = Paper.book().read<Double>("latSos")
        val longitude = Paper.book().read<Double>("longSos")
        val macAddress = Paper.book().read<String>("macAddress")
        val identificador = Paper.book().read<String>("identificador")
        val accuracy = Paper.book().read<String>("accuracy")
        val speedMs = Paper.book().read<Float>("speed")
        val altitude = Paper.book().read<Double>("altitude")
        val speed = (speedMs!!*3.6)
        date = dateFormat.format(Date())

            val result = mGpsViewModel.gpsLocationServer( UserInfo(
                mensaje = "RegistraPosicion",
                usuario = "rodrigotag",
                longitud = longitude!!,
                latitud = latitude!!,
                tagkey = macAddress!!,
                contrasena = "1234",
                codigo = "20",
                fechahora = date,
                identificador = identificador!!,
                satelites = accuracy!!.toInt(),
                velocidad = speed,
                altitud = altitude!!))

            Log.d(TAG, result.toString())
    }

    private fun reeScan(){
        rxBleClient.scanBleDevices(scanSettings(), scanFilterReconnect())
            .firstElement()
            .subscribe(
                { scanResult ->
                    stablesConnection(scanResult.bleDevice)
                }, onError())
    }

    private fun scanFilterReconnect(): ScanFilter =
        ScanFilter.Builder()
            .setDeviceAddress(Paper.book().read("macAddress"))
            .build()

    private fun scanSettings(): ScanSettings =
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    private fun scanFilter(): ScanFilter =
        ScanFilter.Builder()
            .setServiceUuid(serviceUUID)
            .build()

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = Notification
            .Builder(this, "NOTIFICATION")
            .setContentText("Skytag")
            .setSmallIcon(R.drawable.ic_satellite)
            .setContentIntent(pendingIntent)
            .setTicker("ticket")
            .build()

        startForeground(123, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "NOTIFICATION", "My_channel",
            NotificationManager.IMPORTANCE_LOW)

        val manager = getSystemService(
            NotificationManager::class.java)

        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class MyBinder : Binder(){
        fun getService() : BleService {
            return this@BleService
        }
    }

    private fun onError(): (Throwable) -> Unit {
        return { throwable -> throwable.message?.let { Log.e("ERROR BLUETOOTH", it)
            }
        }
    }

    private fun reconnect(): (Throwable) -> Unit {
        return { throwable -> throwable.message?.let { Log.e(TAG, it) }
            reeScan()
            Paper.book().delete("macAddress")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}

