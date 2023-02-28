package com.example.skytagbeta.base.db

import android.app.Application
import androidx.room.Room

class StatusListApplication: Application() {

    companion object{
        lateinit var database: StatusListDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            StatusListDatabase::class.java,
            "StatusListDatabase")
            .build()
    }
}