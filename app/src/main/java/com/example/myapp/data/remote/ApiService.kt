package com.example.myapp.data.remote

import com.example.myapp.data.remote.model.ApiObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("objects")
    suspend fun getObjects(): Response<List<ApiObject>>

    @GET("objects/{id}")
    suspend fun getObject(@Path("id") id: String): Response<ApiObject>

    @PUT("objects/{id}")
    suspend fun updateObject(
        @Path("id") id: String,
        @Body obj: ApiObject
    ): Response<ApiObject>

    @DELETE("objects/{id}")
    suspend fun deleteObject(@Path("id") id: String): Response<Unit>

    companion object {
        const val BASE_URL = "https://api.restful-api.dev/"
    }
}