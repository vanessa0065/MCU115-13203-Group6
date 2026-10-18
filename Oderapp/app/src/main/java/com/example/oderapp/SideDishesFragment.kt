package com.example.oderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class SideDishesFragment : Fragment() {

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_side_dishes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbFries = view.findViewById<CheckBox>(R.id.cb_fries)
        val cbSalad = view.findViewById<CheckBox>(R.id.cb_salad)
        val cbCornCup = view.findViewById<CheckBox>(R.id.cb_corn_cup)
        val btnDone = view.findViewById<Button>(R.id.btn_done)

        vm.order.observe(viewLifecycleOwner) { order ->
            val hasFries = order.sideDishes.contains("Fries")
            val hasSalad = order.sideDishes.contains("Salad")
            val hasCornCup = order.sideDishes.contains("Corn Cup")

            if (cbFries.isChecked != hasFries) cbFries.isChecked = hasFries
            if (cbSalad.isChecked != hasSalad) cbSalad.isChecked = hasSalad
            if (cbCornCup.isChecked != hasCornCup) cbCornCup.isChecked = hasCornCup
        }

        cbFries.setOnCheckedChangeListener { _, isChecked ->
            vm.toggleSideDish("Fries", isChecked)
        }
        cbSalad.setOnCheckedChangeListener { _, isChecked ->
            vm.toggleSideDish("Salad", isChecked)
        }
        cbCornCup.setOnCheckedChangeListener { _, isChecked ->
            vm.toggleSideDish("Corn Cup", isChecked)
        }

        btnDone.setOnClickListener {
            (activity as? MainActivity)?.navigateToPage(2)
        }
    }
}
