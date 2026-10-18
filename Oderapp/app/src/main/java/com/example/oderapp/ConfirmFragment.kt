package com.example.oderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ConfirmFragment : Fragment() {

    private val vm: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvOrderSummary = view.findViewById<TextView>(R.id.tv_order_summary)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)

        vm.order.observe(viewLifecycleOwner) { order ->
            if (order.mainMeal == null && order.sideDishes.isEmpty() && order.drink == null) {
                tvOrderSummary.text = "Your order summary will appear here."
            } else {
                val mainText = order.mainMeal ?: "--"
                val sidesText = if (order.sideDishes.isEmpty()) "--" else order.sideDishes.joinToString(", ")
                val drinkText = order.drink ?: "--"
                tvOrderSummary.text = "Main: $mainText\nSides: $sidesText\nDrink: $drinkText"
            }
        }

        btnConfirm.setOnClickListener {
            // Confirm page: validate
            val order = vm.order.value ?: Order()
            val missingText = order.getMissingItemsText()
            if (missingText != null) {
                Toast.makeText(
                    requireContext(),
                    missingText,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Show AlertDialog with Submit
            AlertDialog.Builder(requireContext())
                .setTitle("Submit Order")
                .setMessage(
                    "Main: ${order.mainMeal}\n" +
                    "Sides: ${order.sideDishes.joinToString(", ")}\n" +
                    "Drink: ${order.drink}\n\nSubmit this order?"
                )
                .setPositiveButton("Submit") { d, _ ->
                    d.dismiss()
                    Toast.makeText(requireContext(), "Order submitted! 🎉", Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.showMainMenu()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
