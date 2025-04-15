package com.example.myapp.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.myapp.databinding.FragmentSettingsBinding
import com.example.myapp.ui.MainActivity
import com.example.myapp.ui.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        binding.switchDarkTheme.isChecked = sharedPref.getBoolean("dark_theme_enabled", false)

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_theme_enabled", isChecked).apply()
            (requireActivity() as MainActivity).applyThemeChange()
        }


        // Observe the current theme state
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.switchDarkTheme.isChecked = isDarkTheme
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkThemeEnabled(isChecked)
            requireActivity().recreate() // Apply theme change immediately
        }
        // Set up the switch listener
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkThemeEnabled(isChecked)
        }

//        // Set up the switch listener
//        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setDarkThemeEnabled(isChecked)
//            requireActivity().recreate() // Restart activity to apply theme
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}