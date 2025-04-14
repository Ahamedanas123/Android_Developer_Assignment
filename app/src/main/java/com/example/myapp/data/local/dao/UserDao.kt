package com.example.myapp.data.local.dao

import androidx.room.*
import com.example.myapp.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Delete
    suspend fun delete(user: UserEntity)
}