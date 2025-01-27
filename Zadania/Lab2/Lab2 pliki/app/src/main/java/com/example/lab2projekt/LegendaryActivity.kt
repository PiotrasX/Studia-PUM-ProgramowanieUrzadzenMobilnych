package com.example.lab2projekt

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

class LegendaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LegendaryScreen()
        }
    }
}

@Composable
fun LegendaryScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(200, 255, 200, 150)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                val explicitIntent = Intent(context, MainActivity::class.java)
                context.startActivity(explicitIntent)
            }) {
                Text(text = "Przejdź do pierwszej aktywności")
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(onClick = {
                val explicitIntent = Intent(context, SecondActivity::class.java)
                context.startActivity(explicitIntent)
            }) {
                Text(text = "Przejdź do drugiej aktywności")
            }
        }

        Spacer(modifier = Modifier.height(175.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Witam!",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row {
                Button(
                    onClick = {
                        Toast.makeText(context, "Cześć", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 153, 204)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Cześć",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Dzień dobry", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(255, 136, 0)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Dzień dobry",
                        color = Color(12, 6, 0),
                        fontSize = 18.sp
                    )
                }
            }

            Row {
                Button(
                    onClick = {
                        val intentYouTube = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=cld3qCjaUEI"))
                        try {
                            context.startActivity(intentYouTube)
                        } catch (e : ActivityNotFoundException) {
                            Toast.makeText(context, "Nie można otworzyć podanego filmu, brak odpowiedniej aplikacji!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(204, 0, 0)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "YouTube",
                        color = Color(254, 251, 251),
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"))
                        context.startActivity(intentGoogle)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 0, 0)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Nowa aktywność",
                        color = Color(242, 177, 47),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LegendaryScreenPreview() {
    LegendaryScreen()
}
