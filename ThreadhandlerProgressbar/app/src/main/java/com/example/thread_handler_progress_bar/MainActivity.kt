package com.example.thread_handler_progress_bar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.thread_handler_progress_bar.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WorkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_start)
                1 -> getString(R.string.tab_progress)
                2 -> getString(R.string.tab_result)
                else -> ""
            }
        }.attach()

        viewModel = ViewModelProvider(this)[WorkViewModel::class.java]

        viewModel.taskState.observe(this) { state ->
            when (state) {
                is TaskState.Preparing -> {
                    // Switch to Progress Fragment (Page 1) when Start is pressed
                    binding.viewPager.setCurrentItem(1, true)
                }
                is TaskState.Finished -> {
                    // Switch to Result Fragment (Page 2) when background work finishes
                    binding.viewPager.setCurrentItem(2, true)
                }
                else -> {}
            }
        }
    }

    private class ViewPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StartFragment()
                1 -> ProgressFragment()
                2 -> ResultFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
