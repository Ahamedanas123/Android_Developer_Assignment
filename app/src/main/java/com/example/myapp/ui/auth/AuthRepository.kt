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
        // 1. Authenticate with Firebase (optional if you're using Firebase)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val firebaseUser = firebaseAuth.signInWithCredential(credential).await().user

        // 2. Create user object
        val user = UserEntity(
            uid = account.id ?: "",
            name = account.displayName ?: "",
            email = account.email ?: "",
            photoUrl = account.photoUrl?.toString() ?: ""
        )

        // 3. Store in local database
        userDao.insertOrUpdate(user)

        return user
    }
}