package com.example.skytagbeta.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseRetrofit {
    private val okHttpClient = OkHttpClient()

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

}