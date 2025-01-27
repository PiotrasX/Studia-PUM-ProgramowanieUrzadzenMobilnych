package com.example.lab5projekt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
// noinspection ExifInterface
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Screen1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen1Fun(navController = rememberNavController())
        }
    }
}

@Composable
fun Screen1Fun(navController: NavHostController) {
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

        // Zmienna do uprawnień kamery
        val cameraPermissionGranted = remember { mutableStateOf(false) }

        // Zmienne do przechowywania zdjęcia jako bitmapy
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        val photoUri = remember { mutableStateOf<Uri?>(null) }

        // Launcher do żądania uprawnień
        val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            cameraPermissionGranted.value = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Kamera wymaga włączenia uprawnień", Toast.LENGTH_SHORT).show()
            }
        }

        // Launcher do aparatu
        val openCameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                photoUri.value?.let { uri ->
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val imageBitmap = BitmapFactory.decodeStream(inputStream)

                        inputStream?.close()

                        val adjustedBitmap = adjustBitmapOrientation(context, uri, imageBitmap)

                        bitmap.value = adjustedBitmap
                        saveLastImagePath(context, uri.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Nie udało się załadować zdjęcia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            val lastImageUri = getLastImagePath(context)
            if (lastImageUri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(lastImageUri)
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)

                    inputStream?.close()

                    val adjustedBitmap = adjustBitmapOrientation(context, lastImageUri, imageBitmap)

                    bitmap.value = adjustedBitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Ostatnie zdjęcie nie istnieje lub nie można go odczytać", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Brak zapisanego zdjęcia", Toast.LENGTH_SHORT).show()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(330.dp, 330.dp)
                        .border(2.dp, Color.Gray, RectangleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap.value != null) {
                        Image(
                            bitmap = bitmap.value!!.asImageBitmap(),
                            contentDescription = "Zdjęcie zrobione kamerą",
                            modifier = Modifier.size(350.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher),
                            contentDescription = "Domyślna ikona",
                            modifier = Modifier.size(350.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .padding(0.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Magenta, Color.Blue)
                            )
                        )
                ) {
                    Button(
                        onClick = {
                            if (cameraPermissionGranted.value) {
                                val file = createImageFile(context)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                photoUri.value = uri
                                saveLastImagePath(context, file.absolutePath)
                                photoUri.value?.let { safeUri ->
                                    openCameraLauncher.launch(safeUri)
                                }
                            } else {
                                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Zrób zdjęcie",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

// Funkcje do zapisywania i ładowania ścieżki ostatniego zdjęcia
private const val PREFS_NAME = "camera_prefs"
private const val KEY_LAST_IMAGE = "last_image_path"

fun saveLastImagePath(context: Context, uri: String) {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(KEY_LAST_IMAGE, uri).apply()
}

fun getLastImagePath(context: Context): Uri? {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val path = sharedPreferences.getString(KEY_LAST_IMAGE, null)
    return if (path != null) {
        if (path.startsWith("content://")) { Uri.parse(path) }
        else { Uri.fromFile(File(path)) }
    } else { null }
}

fun adjustBitmapOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
    try {
        // Próba otwarcia strumienia danych dla metadanych EXIF
        val inputStream = if (uri.scheme == "content") {
            context.contentResolver.openInputStream(uri)
        } else {
            // Jeśli URI to lokalna ścieżka pliku, użyj FileInputStream
            File(uri.path!!).inputStream()
        } ?: return bitmap

        // Pobranie orientacji
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        // Ustawienie macierzy transformacji na podstawie orientacji
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        inputStream.close()

        // Obracanie bitmapy i zwracanie nowej
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        e.printStackTrace()

        // W razie problemów zwracanie oryginalnej bitmapy
        return bitmap
    }
}

@Preview(showBackground = true)
@Composable
fun Screen1FunPreview() {
    Screen1Fun(navController = rememberNavController())
}
