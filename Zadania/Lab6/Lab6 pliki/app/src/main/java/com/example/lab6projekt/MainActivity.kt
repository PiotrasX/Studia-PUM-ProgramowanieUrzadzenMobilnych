package com.example.lab6projekt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current

    var recognizedText by remember { mutableStateOf("") }
    var torchOn by remember { mutableStateOf(false) }
    var torchText by remember { mutableStateOf("Latarka wyłączona") }
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList[0]
    var luxValue by remember { mutableStateOf("Natężenie światła (lux): ") }
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }

    if (lightSensor == null) {
        luxValue = "Czujnik światła nie jest dostępny na tym urządzeniu :("
    }

    // Obsługa mowy
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            recognizedText = resultText ?: "Nie rozpoznano tekstu!"
        } else {
            Toast.makeText(context, "Nie udało się rozpoznać mowy!", Toast.LENGTH_LONG).show()
        }
    }

    // Obsługa natężenia światła
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    if (lightSensor != null) {
                        luxValue = "Natężenie światła (lux): ${event.values[0].toInt()} / ${lightSensor.maximumRange.toInt()}"
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
        }
    }

    // Rejestrowanie i wyrejestrowanie obsługi natężenia światła
    DisposableEffect(Unit) {
        if (lightSensor != null) {
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        onDispose {
            if (lightSensor != null) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = recognizedText,
            modifier = Modifier.padding(top = 128.dp)
        )

        Button(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault()
                    )
                }
                speechRecognizerLauncher.launch(intent)
            }
        ) {
            Text("Kliknij, aby mówić")
        }

        Button(
            onClick = {
                try {
                    torchOn = !torchOn
                    cameraManager.setTorchMode(cameraId, torchOn)
                    torchText = if (torchOn) "Latarka włączona" else "Latarka wyłączona"
                } catch (e: Exception) {
                    e.printStackTrace()
                    torchText = "Błąd w obsłudze latarki"
                }
            }
        ) {
            Text(torchText)
        }

        Text(
            text = luxValue,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    MainApp()
}
