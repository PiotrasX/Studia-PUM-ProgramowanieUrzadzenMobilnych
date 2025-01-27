package com.example.lab1projekt

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout)

        findViewById<Button>(R.id.button_czesc).setOnClickListener {
            findViewById<TextView>(R.id.textView_witam).text = "Cześć!"
        }

        findViewById<Button>(R.id.button_dzienDobry).setOnClickListener {
            findViewById<TextView>(R.id.textView_witam).text = "Dzień dobry!"
        }

        findViewById<Button>(R.id.button_toast).setOnClickListener {
            findViewById<TextView>(R.id.textView_witam).text = "Witam!"
            val message = Toast.makeText(applicationContext, "Witam!", Toast.LENGTH_LONG)
            message.show()
        }
    }
}
