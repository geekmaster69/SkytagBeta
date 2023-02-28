package com.example.skytagbeta.presentation.main.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.presentation.main.service.model.UserInfo
import com.example.skytagbeta.presentation.main.service.viewmodel.ServiceViewModel
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*

private lateinit var dateFormat: SimpleDateFormat
private lateinit var date: String
private const val TAG = "UpdateLocationWorker"
class UpdateLocationWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {

        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val mGpsViewModel = ServiceViewModel()
        Paper.init(applicationContext)
        val latitude = Paper.book().read<Double>("latSos")
        val longitude = Paper.book().read<Double>("longSos")
        val macAddress = Paper.book().read<String>("macAddress")
        val identificador = Paper.book().read<String>("identificador")
        date = dateFormat.format(Date())


        return try {
            val result = mGpsViewModel.gpsLocationServer( UserInfo(
                mensaje = "RegistraPosicion",
                usuario = "rodrigotag",
                longitud = longitude!!,
                latitud = latitude!!,
                tagkey = macAddress!!,
                contrasena = "1234",
                codigo = "3",
                fechahora = date,
                identificador = identificador!!))

            Log.d(TAG, "$result")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error update location")
            showToast(applicationContext, e.message.toString())

            Result.failure()
        }
    }

}