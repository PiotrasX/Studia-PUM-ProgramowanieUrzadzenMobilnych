package com.example.lab5projekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

// Nawigacja po ekranach
@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Kontroler nawigacji

    NavHost(
        navController = navController,
        startDestination = "appNavigation" // Ekran startowy
    ) {
        composable("screen1") {
            Screen1Fun(navController) // Wywołanie funkcji ekranu 1
        }
        composable("screen2") {
            Screen2Fun(navController) // Wywołanie funkcji ekranu 2
        }
        composable("screen3") {
            Screen3Fun(navController) // Wywołanie funkcji ekranu 3
        }
        composable("screen4") {
            Screen4Fun(navController) // Wywołanie funkcji ekranu 4
        }
        composable("appNavigation") {
            AppNavigationFun(navController) // Wywołanie funkcji ekranu AppNavigation
        }
    }
}

@Composable
fun AppNavigationFun(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("screen1") },
                modifier = Modifier.padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Kamera",
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("screen2") },
                modifier = Modifier.padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(192, 192, 192),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Galeria",
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("screen3") },
                modifier = Modifier.padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255, 127, 0),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Odtwarzacz muzyki",
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("screen4") },
                modifier = Modifier.padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Odtwarzacz filmów",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationFunPreview() {
    AppNavigationFun(navController = rememberNavController())
}
