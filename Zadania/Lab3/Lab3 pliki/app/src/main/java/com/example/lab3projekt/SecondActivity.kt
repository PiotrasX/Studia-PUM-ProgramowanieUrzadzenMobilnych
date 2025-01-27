package com.example.lab3projekt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var login = intent.getStringExtra("login") ?: "N/A"
        var password = intent.getStringExtra("password") ?: "N/A"
        var email = intent.getStringExtra("email") ?: "N/A"
        var gender = intent.getStringExtra("gender") ?: "Empty"
        var newsletter = intent.getBooleanExtra("newsletter", false)

        if (login.isEmpty()) login = "N/A"
        if (password.isEmpty()) password = "N/A"
        if (email.isEmpty()) email = "N/A"
        if (gender.isEmpty()) gender = "Empty"

        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            SecondScreen(login, password, email, gender, newsletter)
        }
    }
}

@Composable
fun SecondScreen(
    login: String,
    password: String,
    email: String,
    gender: String,
    newsletter: Boolean
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { focusManager.clearFocus() }
                )
            }
    ) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Login: $login",
                fontSize = 18.sp,
            )

            Text(
                text = "Hasło: $password",
                fontSize = 18.sp,
            )

            Text(
                text = "E-mail: $email",
                fontSize = 18.sp,
            )

            Text(
                text = "Płeć: $gender",
                fontSize = 18.sp,
            )

            Text(
                text = "Newsletter: $newsletter",
                fontSize = 18.sp,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { focusManager.clearFocus() }
                    )
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = "Main activity"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen(
        "Jan",
        "HASH",
        "jan1337@gmail.com",
        "Empty",
        false
    )
}
