package com.example.myapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapp.data.local.dao.ApiObjectDao
import com.example.myapp.data.local.dao.UserDao
import com.example.myapp.data.local.entity.ApiObjectEntity
import com.example.myapp.data.local.entity.UserEntity

@Database(
    entities = [ApiObjectEntity::class, UserEntity::class], // Note the ::class syntax
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apiObjectDao(): ApiObjectDao
    abstract fun userDao(): UserDao
}