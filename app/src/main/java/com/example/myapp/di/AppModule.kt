package com.example.myapp.di

import android.content.Context
import com.example.myapp.data.local.dao.ApiObjectDao
import com.example.myapp.data.local.dao.UserDao
import com.example.myapp.data.remote.ApiService
import com.example.myapp.data.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        apiService: ApiService,
        apiObjectDao: ApiObjectDao,
        userDao: UserDao,
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepository(apiService, apiObjectDao, userDao, context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
}