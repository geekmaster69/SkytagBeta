package com.example.skytagbeta.presentation.main.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "StatusEntity")
data class StatusListEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var accuracy: String = "",
    var gps: String = "",
    var date: String = "",
    var battery: String = "",
    var ble: String = "",
    var network: String = ""
): Serializable
