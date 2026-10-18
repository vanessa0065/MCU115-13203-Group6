package com.example.oderapp

data class Order(
    val mainMeal: String? = null,
    val sideDishes: List<String> = emptyList(),
    val drink: String? = null,
) {
    fun isComplete(): Boolean {
        return getMissingItemsText() == null
    }

    fun getMissingItemsText(): String? {
        val missing = mutableListOf<String>()
        if (mainMeal.isNullOrEmpty()) missing.add("Main meal")
        if (sideDishes.isEmpty()) missing.add("Side dish")
        if (drink.isNullOrEmpty()) missing.add("Drink")

        if (missing.isEmpty()) return null

        return when (missing.size) {
            1 -> "${missing[0]} is not selected, please go select it."
            2 -> "${missing[0]} and ${missing[1]} are not selected, please go select them."
            else -> "Main meal, Side dish, and Drink are not selected, please go select them."
        }
    }
}
