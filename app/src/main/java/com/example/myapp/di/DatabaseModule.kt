package com.example.myapp.di

import android.content.Context
import androidx.room.Room
import com.example.myapp.data.local.AppDatabase
import com.example.myapp.data.local.dao.ApiObjectDao
import com.example.myapp.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides
    fun provideApiObjectDao(database: AppDatabase): ApiObjectDao {
        return database.apiObjectDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}