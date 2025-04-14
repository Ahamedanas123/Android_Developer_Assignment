package com.example.myapp.ui.image

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapp.databinding.FragmentImageBinding
import com.example.myapp.ui.image.ImageViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ImageFragment : Fragment() {
    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ImageViewModel by viewModels()

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Create a copy of the image in your app's private storage
                val copiedUri = copyImageToAppStorage(it)
                viewModel.setImageUri(copiedUri)
                binding.ivPreview.setImageURI(copiedUri)
            } catch (e: Exception) {
                showToast("Failed to load image")
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Handle successful capture
            viewModel.imageUri.value?.let { viewModel.setImageUri(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners
        binding.btnCapture.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnSelect.setOnClickListener {
            checkStoragePermission()
        }

        // Collect from the StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageUri.collect { uri ->
                    uri?.let {
                        binding.ivPreview.setImageURI(it)
                    }
                }
            }
        }
    }

    private fun checkCameraPermission() {
        if (!isAdded || context == null) return

        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    try {
                        launchCamera()
                    } catch (e: Exception) {
                        showToast("Camera error: ${e.localizedMessage}")
                    }
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showToast("Camera permission required")
                }
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkStoragePermission() {
        if (!isAdded || context == null) return

        // For Android 10+ we don't need READ_EXTERNAL_STORAGE for gallery access
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        Dexter.withContext(requireContext())
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    openGallery()
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showToast("Permission required to access photos")
                }
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun launchCamera() {
        try {
            // Create a temporary file in external cache directory
            val storageDir = requireContext().externalCacheDir
            val photoFile = File.createTempFile(
                "IMG_${System.currentTimeMillis()}",
                ".jpg",
                storageDir
            ).apply {
                createNewFile()
            }

            // Get URI using FileProvider
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            // Store the URI and launch camera
            viewModel.setImageUri(photoUri)
            cameraLauncher.launch(photoUri)
        } catch (e: IOException) {
            showToast("Failed to create image file")
        } catch (e: IllegalArgumentException) {
            showToast("Failed to get file URI")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Create an extension function for safe Toast
    fun Fragment.showToast(message: String) {
        if (isAdded && context != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    // Replace your current gallery launcher with this:


    private fun openGallery() {
        try {
            galleryLauncher.launch("image/*")
        } catch (e: ActivityNotFoundException) {
            showToast("No gallery app available")
        }
    }

    private fun copyImageToAppStorage(uri: Uri): Uri {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val outputFile = File(storageDir, "IMG_${timeStamp}.jpg")

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            outputFile
        )
    }

}