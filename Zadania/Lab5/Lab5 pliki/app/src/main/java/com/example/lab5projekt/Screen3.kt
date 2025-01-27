package com.example.lab5projekt

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class Screen3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen3Fun(navController = rememberNavController())
        }
    }
}

@Composable
fun Screen3Fun(navController: NavHostController) {
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

        // SharedPreferences do zapamiętania indeksu piosenki
        val sharedPreferences = context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE)

        // Lista utworów
        val songList = context.resources.getStringArray(R.array.songs)

        // Nazwa bieżącej piosenki
        var currentSongName by remember { mutableStateOf("") }

        // Indeks bieżącej piosenki
        var currentSongIndex by remember {
            mutableIntStateOf(sharedPreferences.getInt("lastSongIndex", 0))
        }

        var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
        var mediaStateText by remember { mutableStateOf("Status: niezainicjalizowany") }
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
        fun playMusic(forceReset: Boolean = false) {
            val songName = songList.getOrNull(currentSongIndex)
            if (mediaPlayer == null || forceReset) {
                if (songName != null) {
                    val resId = context.resources.getIdentifier(songName, "raw", context.packageName)
                    if (resId != 0) {
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(context, resId).apply {
                            setOnCompletionListener {
                                mediaPlayer?.release()
                                mediaPlayer = null
                                mediaStateText = "Utwór zakończony: $currentSongName"
                            }
                            start()
                        }
                        currentSongName = songName
                        mediaStateText = "Odtwarzanie: $currentSongName"
                    } else {
                        mediaStateText = "Nie znaleziono pliku: $songName"
                    }
                } else {
                    mediaStateText = "Brak utworów w liście"
                }
            } else if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
                mediaStateText = "Odtwarzanie: $currentSongName"
            }
        }

        fun pauseMusic() {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaStateText = "Pauza"
            }
        }

        fun stopMusic() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            mediaStateText = "Stop"
        }

        // Obsługa zatrzymania muzyki przy wyjściu z aktywności
        DisposableEffect(Unit) {
            onDispose {
                stopMusic() // Zatrzymanie muzyki i zwolnienie zasobów
                sharedPreferences.edit().putInt("lastSongIndex", currentSongIndex).apply() // Zapisanie indeksu ostatniej piosenki
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Odtwarzacz muzyki",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Przyciski do sterowania
            MusicButton(
                text = "START",
                onClick = { playMusic() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MusicButton(
                text = "PAUZA",
                onClick = { pauseMusic() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MusicButton(
                text = "STOP",
                onClick = { stopMusic() }
            )

            // Status odtwarzacza
            Text(
                text = mediaStateText,
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
                // Przyciski do zmiany utworów
                MusicButton(
                    text = "NASTĘPNY",
                    onClick = {
                        currentSongIndex = (currentSongIndex + 1) % songList.size
                        playMusic(forceReset = true)
                    },
                    buttonColor = Color(234, 59, 143),
                    contentColor = Color.Black,
                    width = 150.dp,
                )
                Spacer(modifier = Modifier.width(16.dp))
                MusicButton(
                    text = "POPRZEDNI",
                    onClick = {
                        currentSongIndex = if (currentSongIndex - 1 < 0) songList.size - 1 else currentSongIndex - 1 // Zmniejsz indeks (z pętlą)
                        playMusic(forceReset = true)
                    },
                    buttonColor = Color(234, 59, 143),
                    contentColor = Color.Black,
                    width = 150.dp,
                )
            }
        }
    }
}

@Composable
fun MusicButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color(0xFF6200EE), // Domyślny kolor przycisku
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
fun Screen3FunPreview() {
    Screen3Fun(navController = rememberNavController())
}
