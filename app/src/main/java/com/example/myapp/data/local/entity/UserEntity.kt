package com.example.myapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val isLoggedIn: Boolean = false,  // To track login state
    val loginTimestamp: Long = System.currentTimeMillis()
)