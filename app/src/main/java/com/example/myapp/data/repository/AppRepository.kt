package com.example.myapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapp.data.local.dao.ApiObjectDao
import com.example.myapp.data.local.dao.UserDao
import com.example.myapp.data.local.entity.ApiObjectEntity
import com.example.myapp.data.local.entity.UserEntity
import com.example.myapp.data.remote.ApiService
import com.example.myapp.data.remote.model.ApiObject
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AppRepository @Inject constructor (
    private val apiService: ApiService,
    private val apiObjectDao: ApiObjectDao,
    private val userDao: UserDao,
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    // API object operations
    suspend fun fetchObjects() {
        try {
            val response = apiService.getObjects()
            if (response.isSuccessful) {
                response.body()?.let { objects ->
                    val entities = objects.map { obj ->
                        ApiObjectEntity(
                            id = obj.id,
                            name = obj.name,
                            color = obj.data?.color,
                            capacity = obj.data?.capacity,
                            price = obj.data?.price

                        )
                    }
                    apiObjectDao.insertAll(entities) // Now this will work
                }
            } else {
                throw Exception("API call failed: ${response.code()}")
            }
        } catch (e: Exception) {
            // Handle error (log or rethrow)
            throw e
        }
    }

    // Image handling
    suspend fun saveImage(bitmap: Bitmap): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(context.getExternalFilesDir(null), "IMG_$timeStamp.jpg")
        withContext(Dispatchers.IO) {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }
        return imageFile.absolutePath
    }

    // Settings
//    suspend fun setNotificationsEnabled(enabled: Boolean) {
//        context.dataStore.edit { settings ->
//            settings[NOTIFICATIONS_KEY] = enabled
//        }
//    }

//    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
//        .map { preferences -> preferences[NOTIFICATIONS_KEY] ?: true }

//    companion object {
//        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
//    }




    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
    }

    // Change to Flow
    val isDarkTheme: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[DARK_THEME_KEY] = enabled
        }
    }
}