package com.example.lab5projekt

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class Screen4 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen4Fun(navController = rememberNavController())
        }
    }
}

@Composable
fun Screen4Fun(navController: NavHostController) {
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

        // SharedPreferences do zapamiętania indeksu wideo
        val sharedPreferences = context.getSharedPreferences("VideoPlayerPrefs", Context.MODE_PRIVATE)

        // Lista wideo
        val videoList = context.resources.getStringArray(R.array.video)

        // Stan odtwarzacza wideo
        var videoView: VideoView? by remember { mutableStateOf(null) }
        var currentVideoIndex by remember {
            mutableIntStateOf(sharedPreferences.getInt("lastVideoIndex", 0))
        }
        var currentVideoName by remember { mutableStateOf(videoList[currentVideoIndex]) }
        var videoStateText by remember { mutableStateOf("Status: niezainicjalizowany") }
        var hasPermission by remember { mutableStateOf(false) }

        // Launcher do uprawnień
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                hasPermission = true
            } else {
                Toast.makeText(context, "Brak przyznanych uprawnień", Toast.LENGTH_SHORT).show()
            }
        }

        // Sprawdzanie i prośba o uprawnienia
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        @SuppressLint("DiscouragedApi")
        fun playVideo(forceReset: Boolean = false) {
            videoView?.let { view ->
                if (forceReset) {
                    val resId = context.resources.getIdentifier(currentVideoName, "raw", context.packageName)
                    if (resId != 0) {
                        view.setVideoURI(Uri.parse("android.resource://${context.packageName}/$resId"))
                        view.start()
                        videoStateText = "Odtwarzanie: $currentVideoName"
                    } else {
                        videoStateText = "Nie znaleziono pliku: $currentVideoName"
                    }
                } else if (!view.isPlaying) {
                    view.start()
                    videoStateText = "Odtwarzanie: $currentVideoName"
                }
            }
        }

        fun pauseVideo() {
            videoView?.let { view ->
                if (view.isPlaying) {
                    view.pause()
                    videoStateText = "Pauza"
                }
            }
        }

        fun stopVideo() {
            videoView?.let { view ->
                view.stopPlayback()
                videoStateText = "Stop"
                view.visibility = View.INVISIBLE
                view.postDelayed({
                    view.visibility = View.VISIBLE
                    val resId = context.resources.getIdentifier(currentVideoName, "raw", context.packageName)
                    view.setVideoURI(Uri.parse("android.resource://${context.packageName}/$resId"))
                }, 1)
            }
        }

        // Obsługa zatrzymania wideo przy wyjściu z aktywności
        DisposableEffect(Unit) {
            onDispose {
                sharedPreferences.edit().putInt("lastVideoIndex", currentVideoIndex).apply()
                videoView?.stopPlayback() // Zatrzymanie wideo i zwolnienie zasobów
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Odtwarzacz wideo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(color = Color.Black)
            ) {
                AndroidView(
                    factory = { context ->
                        VideoView(context).apply {
                            val resId = context.resources.getIdentifier(currentVideoName, "raw", context.packageName)
                            if (resId != 0) {
                                setVideoURI(Uri.parse("android.resource://${context.packageName}/$resId"))
                            } else {
                                Toast.makeText(context, "Nie znaleziono pliku wideo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    update = { videoView = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Przyciski do sterowania
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                VideoButton(
                    text = "START",
                    onClick = { playVideo() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                VideoButton(
                    text = "PAUZA",
                    onClick = { pauseVideo() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                VideoButton(
                    text = "STOP",
                    onClick = { stopVideo() }
                )
            }

            // Status odtwarzacza
            Text(
                text = videoStateText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp, top = 48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Przyciski do zmiany filmów
                VideoButton(
                    text = "POPRZEDNI",
                    onClick = {
                        currentVideoIndex = if (currentVideoIndex - 1 < 0) videoList.size - 1 else currentVideoIndex - 1
                        currentVideoName = videoList[currentVideoIndex]
                        playVideo(forceReset = true)
                    },
                    buttonColor = Color(34, 159, 243),
                    contentColor = Color.Black,
                    width = 150.dp,
                )
                Spacer(modifier = Modifier.width(16.dp))
                VideoButton(
                    text = "NASTĘPNY",
                    onClick = {
                        currentVideoIndex = (currentVideoIndex + 1) % videoList.size
                        currentVideoName = videoList[currentVideoIndex]
                        playVideo(forceReset = true)
                    },
                    buttonColor = Color(34, 159, 243),
                    contentColor = Color.Black,
                    width = 150.dp,
                )
            }
        }
    }
}

@Composable
fun VideoButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color(94,123,143), // Domyślny kolor przycisku
    contentColor: Color = Color.White, // Domyślny kolor tekstu
    width: Dp = 110.dp, // Domyślna szerokość
    height: Dp = 50.dp // Domyślna wysokość
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen4FunPreview() {
    Screen4Fun(navController = rememberNavController())
}
