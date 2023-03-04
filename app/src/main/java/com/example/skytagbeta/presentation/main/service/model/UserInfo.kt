package com.example.skytagbeta.presentation.main.service.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("usuario")val usuario: String = "",
    @SerializedName("contrasena")val contrasena: String = "",
    @SerializedName("identificador")val  identificador: String = "",
    @SerializedName("fechahora")val  fechahora: String = "",
    @SerializedName("codigo")val   codigo: String = "",
    @SerializedName("latitud")val latitud: Double = 0.0,
    @SerializedName("longitud")val longitud: Double = 0.0,
    @SerializedName("tagkey")val  tagkey: String = "",
    @SerializedName("satelites") val satelites: Int = 0,
    @SerializedName("velocidad") val velocidad: Double = 0.0,
    @SerializedName("altitud") val altitud: Double = 0.0,
    @SerializedName("bateria") val bateria: Double,
/*    @SerializedName("datos") var datos: Datos)


data class Datos(
    @SerializedName("internet")val internet: String= "",
    @SerializedName("gps")val gps: String = "",
    @SerializedName("bluetooth")val bluetooth: String = ""*/


)