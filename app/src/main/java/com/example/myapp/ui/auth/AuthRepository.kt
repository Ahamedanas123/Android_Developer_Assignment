package com.example.myapp.ui.auth

import com.example.myapp.data.local.dao.UserDao
import com.example.myapp.data.local.entity.UserEntity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun authenticateWithGoogle(account: GoogleSignInAccount): UserEntity {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase user is null")

            UserEntity(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: account.displayName ?: "",
                email = firebaseUser.email ?: account.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: account.photoUrl?.toString() ?: "",
                isLoggedIn = true
            ).also {
                userDao.insertOrUpdate(it)
            }
        } catch (e: Exception) {
            throw Exception("Firebase authentication failed: ${e.message}")
        }
    }

    suspend fun getCurrentUser(): UserEntity? {
        return userDao.getLoggedInUser()
    }
}