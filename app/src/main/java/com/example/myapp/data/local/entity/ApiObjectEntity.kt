package com.example.myapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_objects")
data class ApiObjectEntity(
@PrimaryKey val id: String,
val name: String,
val color: String?,
val capacity: String?,
val price: Double?,
val lastUpdated: Long = System.currentTimeMillis()
)