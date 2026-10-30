package com.example.thread_handler_progress_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thread_handler_progress_bar.databinding.FragResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WorkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[WorkViewModel::class.java]

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskState.Idle -> {
                    binding.tvResultStatus.text = getString(R.string.idle)
                }
                is TaskState.Preparing -> {
                    binding.tvResultStatus.text = getString(R.string.preparing)
                }
                is TaskState.Working -> {
                    binding.tvResultStatus.text = getString(R.string.working)
                }
                is TaskState.Finished -> {
                    binding.tvResultStatus.text = getString(R.string.finished)
                }
                is TaskState.Canceled -> {
                    binding.tvResultStatus.text = getString(R.string.canceled)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
