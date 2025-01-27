package com.example.lab4projekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
        composable("mainScreen") {
            MainScreenFun(navController) // Wywołanie funkcji ekranu MainScreen
        }
        composable("secondScreen") {
            SecondScreenFun(navController) // Wywołanie funkcji ekranu SecondScreen
        }
        composable("thirdScreen") {
            ThirdScreenFun(navController) // Wywołanie funkcji ekranu ThirdScreen
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
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Button(
                    onClick = { navController.navigate("mainScreen") },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Idź do MainScreen")
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Button(
                    onClick = { navController.navigate("secondScreen") },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.White
                    )
                ) {
                    Text("Idź do SecondScreen")
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Button(
                    onClick = { navController.navigate("thirdScreen") },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Idź do ThirdScreen")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationFunPreview() {
    AppNavigationFun(navController = rememberNavController())
}
