package com.example.oderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class DrinkFragment : Fragment() {

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgDrink = view.findViewById<RadioGroup>(R.id.rg_drink)
        val rbCola = view.findViewById<RadioButton>(R.id.rb_cola)
        val rbIcedTea = view.findViewById<RadioButton>(R.id.rb_iced_tea)
        val rbWater = view.findViewById<RadioButton>(R.id.rb_water)
        val btnDone = view.findViewById<Button>(R.id.btn_done)

        vm.order.observe(viewLifecycleOwner) { order ->
            when (order.drink) {
                "Cola" -> if (!rbCola.isChecked) rbCola.isChecked = true
                "Iced Tea" -> if (!rbIcedTea.isChecked) rbIcedTea.isChecked = true
                "Water" -> if (!rbWater.isChecked) rbWater.isChecked = true
                else -> rgDrink.clearCheck()
            }
        }

        rgDrink.setOnCheckedChangeListener { _, checkedId ->
            val selectedDrink = when (checkedId) {
                R.id.rb_cola -> "Cola"
                R.id.rb_iced_tea -> "Iced Tea"
                R.id.rb_water -> "Water"
                else -> null
            }
            if (selectedDrink != null) {
                vm.setDrink(selectedDrink)
            }
        }

        btnDone.setOnClickListener {
            (activity as? MainActivity)?.navigateToPage(3)
        }
    }
}
