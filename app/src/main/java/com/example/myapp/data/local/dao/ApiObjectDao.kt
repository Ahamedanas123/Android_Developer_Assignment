package com.example.myapp.data.local.dao

import androidx.room.*
import com.example.myapp.data.local.entity.ApiObjectEntity

@Dao
interface ApiObjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: ApiObjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objects: List<ApiObjectEntity>) // Add this function

    @Query("SELECT * FROM api_objects")
    suspend fun getAll(): List<ApiObjectEntity>

    @Update
    suspend fun update(obj: ApiObjectEntity)

    @Delete
    suspend fun delete(obj: ApiObjectEntity)

    @Query("SELECT * FROM api_objects WHERE id = :id")
    suspend fun getById(id: String): ApiObjectEntity?
}