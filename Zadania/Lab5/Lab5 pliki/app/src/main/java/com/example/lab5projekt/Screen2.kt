package com.example.lab5projekt

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

class Screen2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen2Fun(navController = rememberNavController())
        }
    }
}

@Composable
fun Screen2Fun(navController: NavHostController) {
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
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Powrót do nawigacji",
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Kontekst
        val context = LocalContext.current

        // Pobranie listy obrazów
        var imageList by remember { mutableStateOf(emptyList<String>()) }
        var isPermissionGranted by remember { mutableStateOf(false) }

        // Wywołanie żądania uprawień
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            isPermissionGranted = isGranted
            if (isGranted) {
                imageList = getFirst10Images(context)
            } else {
                Toast.makeText(context, "Brak dostępu do pamięci urządzenia", Toast.LENGTH_SHORT).show()
            }
        }

        // Sprawdzenie i żądanie uprawnień
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = true
                imageList = getFirst10Images(context)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Zmienna do śledzenia aktualnie wyświetlanego obrazu
        var currentImageIndex by remember { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(330.dp, 330.dp)
                    .border(2.dp, Color.Gray, RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                // Wyświetlanie obrazu
                if (imageList.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageList[currentImageIndex]),
                        contentDescription = "Zdjęcie z galerii",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Brak obrazów do wyświetlenia")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (imageList.isNotEmpty() && currentImageIndex > 0) {
                            currentImageIndex -= 1
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = imageList.isNotEmpty() && currentImageIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentImageIndex > 0) Color(109, 125, 0) else Color.Gray,
                        contentColor = Color(232, 254, 213)
                    )
                ) {
                    Text(text = "Poprzedni obraz")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (imageList.isNotEmpty() && currentImageIndex < imageList.size - 1) {
                            currentImageIndex += 1
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = imageList.isNotEmpty() && currentImageIndex < imageList.size - 1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentImageIndex < imageList.size - 1) Color(129, 95, 0) else Color.Gray,
                        contentColor = Color(213, 254, 232)
                    )
                ) {
                    Text(text = "Następny obraz")
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

fun getFirst10Images(context: Context): List<String> {
    val fileList = mutableListOf<String>()
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (it.moveToNext()) {
            val filePath = it.getString(columnIndex)
            fileList.add(filePath)

            if (fileList.size >= 10) break
        }
    }

    return fileList
}

@Preview(showBackground = true)
@Composable
fun Screen2FunPreview() {
    Screen2Fun(navController = rememberNavController())
}
