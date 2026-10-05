package com.example.secactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sec)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sec)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvReceivedText = findViewById<TextView>(R.id.tvReceivedText)
        val etReply = findViewById<EditText>(R.id.etReply)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        // STEP 03 / STEP 04: SecActivity 取得傳送過來的資料並顯示
        val receivedText = intent.getStringExtra("EXTRA_TEXT") ?: ""
        tvReceivedText.text = receivedText

        btnReturn.setOnClickListener {
            val replyText = etReply.text.toString()
            val resultIntent = Intent().apply {
                putExtra("EXTRA_RESULT", replyText)
            }
            // STEP 03: SecActivity 使用 setResult() 方法儲存要回傳的資料
            setResult(RESULT_OK, resultIntent)
            // STEP 04: SecActivity 使用 finish() 方法結束 SecActivity，並回傳到 MainActivity
            finish()
        }
    }
}