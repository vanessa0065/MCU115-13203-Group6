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

class MainMealFragment : Fragment() {

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgMainMeal = view.findViewById<RadioGroup>(R.id.rg_main_meal)
        val rbBurger = view.findViewById<RadioButton>(R.id.rb_burger)
        val rbFriedChicken = view.findViewById<RadioButton>(R.id.rb_fried_chicken)
        val rbChickenNuggets = view.findViewById<RadioButton>(R.id.rb_chicken_nuggets)
        val btnDone = view.findViewById<Button>(R.id.btn_done)

        vm.order.observe(viewLifecycleOwner) { order ->
            when (order.mainMeal) {
                "Burger" -> if (!rbBurger.isChecked) rbBurger.isChecked = true
                "Fried Chicken" -> if (!rbFriedChicken.isChecked) rbFriedChicken.isChecked = true
                "Chicken Nuggets" -> if (!rbChickenNuggets.isChecked) rbChickenNuggets.isChecked = true
                else -> rgMainMeal.clearCheck()
            }
        }

        rgMainMeal.setOnCheckedChangeListener { _, checkedId ->
            val selectedMeal = when (checkedId) {
                R.id.rb_burger -> "Burger"
                R.id.rb_fried_chicken -> "Fried Chicken"
                R.id.rb_chicken_nuggets -> "Chicken Nuggets"
                else -> null
            }
            if (selectedMeal != null) {
                vm.setMainMeal(selectedMeal)
            }
        }

        btnDone.setOnClickListener {
            (activity as? MainActivity)?.navigateToPage(1)
        }
    }
}
