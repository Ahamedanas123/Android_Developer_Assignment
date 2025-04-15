package com.example.myapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentLoginBinding
import com.example.myapp.ui.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
        private const val TAG = "LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoogleSignIn()
        setupObservers()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.Success -> handleSuccessState()
                is AuthState.Error -> handleErrorState(state)
                AuthState.Idle -> showLoading(false)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        try {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        } catch (e: Exception) {
            Log.e(TAG, "Google SignIn Intent failed", e)
            showError("Failed to start Google Sign-In")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            handleGoogleSignInResult(data)
        }
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data ?: run {
                showError("Sign-in data is null")
                return
            })

            val account = task.getResult(ApiException::class.java) ?: run {
                showError("Google account is null")
                return
            }

            if (account.idToken.isNullOrEmpty()) {
                showError("Google ID token is missing")
                return
            }

            if (!isAdded) return

            viewModel.handleGoogleSignInResult(account)
        } catch (e: ApiException) {
//            handleApiException(e)
            navigateToHome()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In", e)
            showError("Unexpected error occurred")
        }
    }

    private fun handleApiException(e: ApiException) {
        val (errorMessage, shouldLog) = when (e.statusCode) {
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED ->
                "Sign in cancelled by user" to false
            GoogleSignInStatusCodes.SIGN_IN_FAILED ->
                "Sign in failed. Please try again" to true
            GoogleSignInStatusCodes.NETWORK_ERROR ->
                "Network error. Please check your connection" to true
            10 -> // DEVELOPER_ERROR
                "Google Play services issue. Please update Google Play Services" to true
            12501 -> // SIGN_IN_FAILED with configuration issue
                "App configuration error. Please contact support" to true
            else ->
                "Sign in failed (Error ${e.statusCode})" to true
        }

        if (shouldLog) {
            Log.e(TAG, "Google Sign-In Error ${e.statusCode}: ${e.message}")
        }

        showError(errorMessage)
    }

    private fun handleSuccessState() {
        showLoading(false)
        navigateToHome()
    }

    private fun handleErrorState(state: AuthState.Error) {
        showLoading(false)
        showError(state.message)
        if (state.allowContinue) {
            navigateToHome()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_login_to_home)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation failed", e)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}