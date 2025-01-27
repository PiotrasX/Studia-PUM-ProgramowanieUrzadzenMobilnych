package com.example.lab1projekt

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@SuppressLint("ShowToast")
@Composable
fun MainScreen() {
    // Tekst służący do modyfikacji
    var message by remember { mutableStateOf("Witam!") }

    // Definicja LocalContext
    val context = LocalContext.current

    // Główny układ menu
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(16.dp))

        // Przyciski
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Przycisk "Cześć!"
            Button(
                onClick = {
                    message = "Cześć!"
                    Toast.makeText(context, "Cześć!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.weight(8f).height(48.dp),
                colors = ButtonDefaults.buttonColors(Color.Blue)
            ) {
                Text(text = "Cześć!")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Przycisk "Toast!"
            Button(
                onClick = {
                    message = "Toast!"
                    Toast.makeText(context, "Toast!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.weight(6f).height(48.dp),
                colors = ButtonDefaults.buttonColors(Color.Green)
            ) {
                Text(text = "Toast!")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Przycisk "Dzień dobry!"
            Button(
                onClick = {
                    message = "Dzień dobry!"
                    Toast.makeText(context, "Dzień dobry!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.weight(8f).height(48.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(text = "Dzień dobry!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
