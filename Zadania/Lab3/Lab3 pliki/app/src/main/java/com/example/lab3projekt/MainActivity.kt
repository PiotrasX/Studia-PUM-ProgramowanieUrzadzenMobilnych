package com.example.lab3projekt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
                MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var warnPassword by remember { mutableStateOf("...") }
    var hasFocusPassword by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var warnEmail by remember { mutableStateOf("...") }
    var hasFocusEmail by remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var checkedStatute by remember { mutableStateOf(false) }
    var checkedNewsletter by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    warnPassword = ObslugaTextEdit.obslugaEditPassword(password)
    warnEmail = ObslugaTextEdit.obslugaEditEmail(email)

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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        text = "Your login"
                    )
                },
                singleLine = true,
                modifier = Modifier.width(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Your password"
                    )
                },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                        focusManager.clearFocus()
                    }) {
                        Icon(image, "Password visibility")
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        hasFocusPassword = focusState.isFocused
                    }
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = warnPassword,
                modifier = Modifier.alpha(if (hasFocusPassword) 1f else 0f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Your e-mail"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .width(200.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        hasFocusEmail = focusState.isFocused
                    }
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = warnEmail,
                modifier = Modifier.alpha(if (hasFocusEmail) 1f else 0f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(
                text = "Set Your sex:",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            RadioButtonItem("Male", selectedOption, focusManager)
            RadioButtonItem("Female", selectedOption, focusManager)
            RadioButtonItem("Other", selectedOption, focusManager)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedStatute,
                onCheckedChange = { checkedStatute = it }
            )

            Text("Akceptuje regulamin")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedNewsletter,
                onCheckedChange = { checkedNewsletter = it }
            )

            Text("Chcę otrzymać newsletter")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { focusManager.clearFocus() }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (warnPassword != "Super hasło!") {
                        Toast.makeText(context, "Popraw hasło!", Toast.LENGTH_SHORT).show()
                    } else if (warnEmail != "Poprawny e-mail!") {
                        Toast.makeText(context, "Popraw e-mail!", Toast.LENGTH_SHORT).show()
                    } else if (!checkedStatute) {
                        Toast.makeText(context, "Zaakceptuj regulamin!", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(context, SecondActivity::class.java).apply {
                            putExtra("login", username)
                            putExtra("password", password)
                            putExtra("email", email)
                            putExtra("gender", selectedOption.value)
                            putExtra("newsletter", checkedNewsletter)
                        }
                        context.startActivity(intent)
                    }

                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = "Potwierdź"
                )
            }
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
                    val intent = Intent(context, SecondActivity::class.java).apply {
                        putExtra("login", username)
                        putExtra("password", password)
                        putExtra("email", email)
                        putExtra("gender", selectedOption.value)
                        putExtra("newsletter", checkedNewsletter)
                    }
                    context.startActivity(intent)
                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = "Second activity"
                )
            }
        }
    }
}

@Composable
fun RadioButtonItem(text: String, selectedOption: MutableState<String>, focusManager: FocusManager) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = (text == selectedOption.value),
            onClick = {
                selectedOption.value = text
                focusManager.clearFocus()
            }
        )
        Text(
            text = text
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
