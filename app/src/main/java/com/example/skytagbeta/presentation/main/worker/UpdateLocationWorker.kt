package com.example.skytagbeta.presentation.main.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytagbeta.base.Constants
import com.example.skytagbeta.base.db.StatusListApplication
import com.example.skytagbeta.base.utils.makeStatusNotification
import com.example.skytagbeta.presentation.locationhistory.entity.StatusListEntity
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.viewmodel.ServiceViewModel
import com.example.skytagbeta.presentation.main.utils.*
import com.google.gson.JsonObject
import io.paperdb.Paper

private const val TAG = "UpdateLocationWorker"
class UpdateLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        makeStatusNotification("Update Location", applicationContext, 1)

        val mGpsViewModel = ServiceViewModel()
        Paper.init(applicationContext)
        val latitude = Paper.book().read<Double>("latSos")?: 0.0
        val longitude = Paper.book().read<Double>("longSos") ?: 0.0
        val macAddress = Paper.book().read<String>("macAddress") ?: "offline"
        val identificador = Paper.book().read<String>("identificador")
        val accuracy = Paper.book().read<String>("accuracy")
        val altitude = Paper.book().read<Double>("altitude")
        val speedMs = Paper.book().read<Float>("speed")
        val speed = (speedMs!!*3.6)
        val battery = getBatteryPercentage(applicationContext).toString()
        val gpsStatus =  (getGpsStatus(applicationContext))
        val networkStatus =  (networkStatus(applicationContext))
        val bleStatus =  bluetoothStatus(applicationContext)
        val date = getDate()
        val jsonObject= JsonObject()
        jsonObject.addProperty("internet", networkStatus(applicationContext))
        jsonObject.addProperty("gps", getGpsStatus(applicationContext))
        jsonObject.addProperty("bluetootn", bluetoothStatus(applicationContext))

        return try {
            val result = mGpsViewModel.gpsLocationServer( UserInfo(
                mensaje = "RegistraPosicion",
                usuario = "rodrigotag",
                longitud = longitude,
                latitud = latitude,
                tagkey = macAddress,
                contrasena = "1234",
                codigo = Constants.UPDATE_LOCATION,
                fechahora = date,
                identificador = identificador!!,
                satelites = accuracy!!.toInt(),
                velocidad = speed,
                altitud = altitude!!,
                bateria = battery.toDouble(),
            ))

            Log.d(TAG, "$result")

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        } finally {
            StatusListApplication.database.statusDao().addStatus(
                StatusListEntity(
                lat = latitude,
                lng = longitude,
                accuracy = accuracy!!,
                battery = "$battery%",
                gps = gpsStatus.toString(),
                network = networkStatus.toString(),
                ble = "$bleStatus $macAddress",
                date = date,
                code = Constants.UPDATE_LOCATION)
            )
        }
    }
}