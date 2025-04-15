package com.example.myapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.ui.auth.AuthState
import com.example.myapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    private var isRecreating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Check authentication state
        checkAuthState(navController)
    }

    private fun checkAuthState(navController: NavController) {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Success -> {
                    // User is logged in, navigate to home
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                }
                is AuthState.Error -> {
                    // Error but might allow continue
                    if (state.allowContinue) {
                        navController.navigate(R.id.homeFragment)
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                }
                else -> {
                    // Idle or Loading - navigate to login
                    if (navController.currentDestination?.id != R.id.loginFragment) {
                        navController.navigate(R.id.loginFragment)
                    }
                }
            }
        }
    }

    private fun setAppTheme() {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isDarkTheme = sharedPref.getBoolean("dark_theme_enabled", false)

        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }
    }

    fun applyThemeChange() {
        if (!isRecreating) {
            isRecreating = true
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        isRecreating = false
    }
}