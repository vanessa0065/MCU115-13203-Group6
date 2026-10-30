package com.example.thread_handler_progress_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thread_handler_progress_bar.databinding.FragStartBinding

class StartFragment : Fragment() {

    private var _binding: FragStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WorkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[WorkViewModel::class.java]

        binding.btnStart.setOnClickListener {
            viewModel.startWork()
        }

        binding.btnCancel.setOnClickListener {
            viewModel.cancelWork()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
