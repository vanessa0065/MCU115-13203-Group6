package com.example.oderapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private val vm: OrderViewModel by viewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var mainMenuContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainMenuContainer = findViewById(R.id.main_menu_container)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = WizardPagerAdapter(this)
        viewPager.offscreenPageLimit = 3

        val btnChooseMainMeal = findViewById<Button>(R.id.btn_choose_main_meal)
        val btnChooseSideDishes = findViewById<Button>(R.id.btn_choose_side_dishes)
        val btnChooseDrink = findViewById<Button>(R.id.btn_choose_drink)
        val btnOrder = findViewById<Button>(R.id.btn_order)
        val tvCurrentSelection = findViewById<TextView>(R.id.tv_current_selection)

        btnChooseMainMeal.setOnClickListener { navigateToPage(0) }
        btnChooseSideDishes.setOnClickListener { navigateToPage(1) }
        btnChooseDrink.setOnClickListener { navigateToPage(2) }
        btnOrder.setOnClickListener { navigateToPage(3) }

        vm.order.observe(this) { order ->
            val mainMealText = order.mainMeal ?: "--"
            val sideDishesText = if (order.sideDishes.isEmpty()) "--" else order.sideDishes.joinToString(", ")
            val drinkText = order.drink ?: "--"

            tvCurrentSelection.text = "Current Selection:\nMeal: $mainMealText\nSides: $sideDishesText\nDrink: $drinkText"
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.visibility == View.VISIBLE) {
                    showMainMenu()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun navigateToPage(page: Int) {
        if (::viewPager.isInitialized) {
            mainMenuContainer.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
            viewPager.currentItem = page
        }
    }

    fun showMainMenu() {
        if (::viewPager.isInitialized && ::mainMenuContainer.isInitialized) {
            viewPager.visibility = View.GONE
            mainMenuContainer.visibility = View.VISIBLE
        }
    }
}
