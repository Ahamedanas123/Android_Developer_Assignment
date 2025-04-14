package com.example.myapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,  // Changed from 'id' to 'uid' to match Firebase
    val name: String?,
    val email: String?,
    val photoUrl: String?,  // Changed from 'profilePicture' to 'photoUrl'
    val lastLogin: Long = System.currentTimeMillis()
)