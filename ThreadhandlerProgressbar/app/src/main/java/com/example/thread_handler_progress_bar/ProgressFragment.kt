package com.example.thread_handler_progress_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thread_handler_progress_bar.databinding.FragProgressBinding

class ProgressFragment : Fragment() {

    private var _binding: FragProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WorkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[WorkViewModel::class.java]

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskState.Idle -> {
                    binding.circularProgressBar.progress = 0
                    binding.tvPercent.text = getString(R.string.percent_format, 0)
                    binding.tvStatus.text = getString(R.string.idle)
                }
                is TaskState.Preparing -> {
                    binding.circularProgressBar.progress = 0
                    binding.tvPercent.text = getString(R.string.percent_format, 0)
                    binding.tvStatus.text = getString(R.string.preparing)
                }
                is TaskState.Working -> {
                    binding.circularProgressBar.progress = state.progress
                    binding.tvPercent.text = getString(R.string.percent_format, state.progress)
                    binding.tvStatus.text = getString(R.string.working_progress, state.progress)
                }
                is TaskState.Finished -> {
                    binding.circularProgressBar.progress = 100
                    binding.tvPercent.text = getString(R.string.percent_format, 100)
                    binding.tvStatus.text = getString(R.string.finished)
                }
                is TaskState.Canceled -> {
                    binding.tvStatus.text = getString(R.string.canceled)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
