package com.example.lab2projekt

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecondScreen()
        }
    }
}

@Composable
fun MainScreen() {
    // Uzyskanie kontekstu
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(255, 200, 200, 150)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {
            // Tworzenie intencji jawnej
            val explicitIntent = Intent(context, SecondActivity::class.java)

            // Wywołanie nowej aktywności (w tym przypadku innego ekranu)
            context.startActivity(explicitIntent)
        }) {
            Text(text = "Przejdź do drugiej aktywności")
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {
            // Tworzenie intencji jawnej
            val explicitIntent = Intent(context, LegendaryActivity::class.java)

            // Wywołanie nowej aktywności (w tym przypadku innego ekranu)
            context.startActivity(explicitIntent)
        }) {
            Text(text = "Przejdź do legendarnej aktywności")
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(onClick = {
            // Tworzenie intencji niejawnej
            val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"))

            // Wywołanie nowej aktywności (w tym przypadku otwarcie Google)
            context.startActivity(intentGoogle)
        }) {
            Text(text = "Przejdź do Google")
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {
            // Tworzenie intencji niejawnej
            val textMessage = "Moja wiadomość do udostępnienia."
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textMessage)
                type = "text/plain"
            }

            // Wywołanie nowej aktywności (w tym przypadku udostępnienie danych)
            try {
                context.startActivity(sendIntent)
            } catch (e : ActivityNotFoundException) {
                // Po wywołaniu funkcji startActivity() system sprawdza wszystkie zainstalowane
                // aplikacje, aby określić, które z nich poradzą sobie z tego rodzaju intencją.
                // Jeśli istnieje tylko jedna aplikacja, która może to obsłużyć, zostanie ona
                // natychmiast otwarta i otrzyma intencję. Jeśli żadna aplikacja nie może obsłużyć
                // wywołanej intencji, nasza aplikacja może przechwycić wyjątek
                // ActivityNotFoundException. Jeśli intencję może zostać obsłużona
                // przez wiele różnych aplikacji, system wyświetli okno dialogowe,
                // dzięki czemu użytkownik może wybrać, której aplikacji chce użyć.
            }
        }) {
            Text(text = "Wyślij dane")
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun SecondScreen() {
    // Uzyskanie kontekstu
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(200, 200, 255, 150)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Button(
            onClick = {
                // Tworzenie intencji niejawnej
                val intentYouTube = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=N5lRVG9P0fg"))

                // Sprawdzenie, czy istnieje aplikacja zdolna obsłużyć taką intencję
                try {
                    // Wywołanie nowej aktywności (w tym przypadku otwarcie YouTube)
                    context.startActivity(intentYouTube)
                } catch (e : ActivityNotFoundException) {
                    //Wywołanie Toast informujący użytkownika, że nie ma odpowiedniej aplikacji do wywołania takiej aktywności
                    Toast.makeText(context, "Nie można otworzyć podanego filmu, brak odpowiedniej aplikacji!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Dzień dobry!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(onClick = {
            // Tworzenie intencji jawnej
            val explicitIntent = Intent(context, LegendaryActivity::class.java)

            // Wywołanie nowej aktywności (w tym przypadku innego ekranu)
            context.startActivity(explicitIntent)
        }) {
            Text(text = "Przejdź do legendarnej aktywności")
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {
            // Tworzenie intencji jawnej
            val explicitIntent = Intent(context, MainActivity::class.java)

            // Wywołanie nowej aktywności (w tym przypadku innego ekranu)
            context.startActivity(explicitIntent)
        }) {
            Text(text = "Przejdź do pierwszej aktywności")
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen()
}
