package com.example.oderapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _order = MutableLiveData(Order())
    val order: LiveData<Order> get() = _order

    fun setMainMeal(meal: String) {
        val currentOrder = _order.value ?: Order()
        _order.value = currentOrder.copy(mainMeal = meal)
    }

    fun toggleSideDish(sideDish: String, isSelected: Boolean) {
        val currentOrder = _order.value ?: Order()
        val updatedSides = currentOrder.sideDishes.toMutableList()
        if (isSelected) {
            if (!updatedSides.contains(sideDish)) {
                updatedSides.add(sideDish)
            }
        } else {
            updatedSides.remove(sideDish)
        }
        _order.value = currentOrder.copy(sideDishes = updatedSides)
    }

    fun setDrink(drink: String) {
        val currentOrder = _order.value ?: Order()
        _order.value = currentOrder.copy(drink = drink)
    }

    fun resetOrder() {
        _order.value = Order()
    }
}
