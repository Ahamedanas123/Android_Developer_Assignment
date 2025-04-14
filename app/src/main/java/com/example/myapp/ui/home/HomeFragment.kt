package com.example.myapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentHomeBinding
import com.example.myapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize click listeners
        binding.cardObjects.setOnClickListener {
            navigateToObjects()
        }

        binding.cardImages.setOnClickListener {
            navigateToImages()
        }

        binding.cardPdf.setOnClickListener {
            navigateToPdf()
        }

        binding.cardSettings.setOnClickListener {
            navigateToSettings()
        }
    }

    private fun navigateToObjects() {
        findNavController().navigate(R.id.action_home_to_objects)
    }

    private fun navigateToImages() {
        findNavController().navigate(R.id.action_home_to_image) // Fixed typo in action ID
    }

    private fun navigateToPdf() {
        findNavController().navigate(R.id.action_home_to_pdf)
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_home_to_settings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}