package com.example.myapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiObject(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("data") val data: ObjectData?
)

data class ObjectData(
    @SerializedName("color") val color: String?,
    @SerializedName("capacity") val capacity: String?,
    @SerializedName("price") val price: Double?
)