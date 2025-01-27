package com.example.lab4projekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.compose.rememberNavController

class MainScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreenFun(navController = rememberNavController())
        }
    }
}

// Odpowiednik ListView
@Composable
fun SimpleList(items: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        items(items) { item ->
            Text(text = item) // Wyświetlanie każdego elementu
        }
    }
}

// Odpowiednik GridView
@Composable
fun SimpleGrid(items: List<String>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Siatka z 3 kolumnami
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Cyan)
    ) {
        items(items) { item ->
            Text(text = item) // Wyświetlanie każdego elementu
        }
    }
}

@Composable
fun MainScreenFun(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate("appNavigation") },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Idź do AppNavigation")
                }
            }
        }

        SimpleList(
            items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
        )

        Box(modifier = Modifier.height(16.dp))

        SimpleGrid(
            items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"),
        )

        Box(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenFunPreview() {
    MainScreenFun(navController = rememberNavController())
}
