package com.example.skytagbeta.presentation.main.gps

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationClient(interval: Long) : Flow<Location>

    class LocationException(message: String) : Exception()
}