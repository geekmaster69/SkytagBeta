package com.example.skytagbeta.base.db.dao

import androidx.room.*
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity

@Dao
interface StatusListDao {

    @Query("SELECT * FROM StatusEntity")
    suspend fun getStatusList() : MutableList<StatusListEntity>

    @Insert
    suspend fun addStatus(statusListEntity: StatusListEntity)

    @Query("SELECT * FROM StatusEntity where id = :id")
    fun getStatusById(id: Long): StatusListEntity

    @Update
    suspend fun updateStatus(statusListEntity: StatusListEntity)

    @Delete
    suspend fun deleteStatus(statusListEntity: StatusListEntity)

    @Query("DELETE FROM StatusEntity")
    suspend fun deleteAllStatus()
}