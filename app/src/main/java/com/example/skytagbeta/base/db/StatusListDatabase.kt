package com.example.skytagbeta.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skytagbeta.base.db.dao.StatusListDao
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity


@Database(entities = [StatusListEntity::class], version = 1)
abstract class StatusListDatabase : RoomDatabase() {

    abstract fun statusDao(): StatusListDao
}