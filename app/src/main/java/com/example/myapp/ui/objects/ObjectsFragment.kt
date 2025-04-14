package com.example.myapp.ui.objects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.databinding.FragmentObjectsBinding
import com.example.myapp.ui.objects.ObjectsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ObjectsFragment : Fragment() {
    private var _binding: FragmentObjectsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ObjectsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentObjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}