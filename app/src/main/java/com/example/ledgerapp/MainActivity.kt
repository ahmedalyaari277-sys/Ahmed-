package com.example.ledgerapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "تطبيق دفتر الحسابات يعمل بنجاح!"
            textSize = 20f
            setPadding(40, 40, 40, 40)
        }
        setContentView(textView)
    }
}
