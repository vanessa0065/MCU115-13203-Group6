package com.example.secactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // STEP 01: MainActivity 宣告 ActivityResultLauncher 作為 Activity 啟動器
    private lateinit var secActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etInput = findViewById<EditText>(R.id.etInput)
        val btnSwitch = findViewById<Button>(R.id.btnSwitch)
        val tvResultText = findViewById<TextView>(R.id.tvResultText)

        // STEP 05: MainActivity 使用 ActivityResultLauncher 取得回傳的資料
        secActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val returnedText = result.data?.getStringExtra("EXTRA_RESULT") ?: ""
                tvResultText.text = returnedText
            }
        }

        // STEP 02: MainActivity 使用 ActivityResultLauncher 發送資料，並前往 SecActivity
        btnSwitch.setOnClickListener {
            val inputText = etInput.text.toString()
            val intent = Intent(this, SecActivity::class.java).apply {
                putExtra("EXTRA_TEXT", inputText)
            }
            secActivityLauncher.launch(intent)
        }
    }
}