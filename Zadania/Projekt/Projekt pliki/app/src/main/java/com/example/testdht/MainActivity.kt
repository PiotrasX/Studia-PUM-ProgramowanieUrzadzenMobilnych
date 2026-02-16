@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.example.testdht

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshWidget(this)
        setContent {
            WifiCheckScreen()
        }
    }

    @Composable
    fun WifiCheckScreen() {
        var isWifiConnected by remember { mutableStateOf(false) }
        var showWifiDialog by remember { mutableStateOf(false) }

        // Zmienna do kontekstu aplikacji
        val context = LocalContext.current

        // Instancja lokalna SharedPreferences
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Uruchamianie efektu sprawdzania WiFi
        LaunchedEffect(Unit) {
            // Monitorowanie w pętli nieskończonej
            while (true) {
                isWifiConnected = checkWifiConnection()
                showWifiDialog = !isWifiConnected // Jeśli brak połączenia, pokazywanie dialogu
                delay(1000) // Sprawdzanie co sekundę
            }
        }

        // Wyświetlanie dialogu, jeśli nie ma połączenia z WiFi
        if (showWifiDialog) {
            // Zmienne motywu jasnego/ciemnego
            val isDarkMode by remember { mutableStateOf(sharedPref.getBoolean("isDarkMode", false)) }
            val colorBackground by remember { mutableStateOf (if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)) }

            // Biały ekran jako tło początkowe
            Box(modifier = Modifier.fillMaxSize().background(colorBackground))

            WifiRequiredDialog()
        }

        // Ładowanie MainScreen gdy połączenie jest nawiązane
        if (isWifiConnected) {
            MainScreen()
        }

        // MainScreen()
    }

    // Funkcja sprawdzająca stan połączenia z WiFi
    private fun checkWifiConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @Composable
    fun WifiRequiredDialog() {
        // Zmienna do kontekstu aplikacji
        val context = LocalContext.current

        // Instancja lokalna SharedPreferences
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Zmienne motywu jasnego/ciemnego
        val isDarkMode by remember { mutableStateOf(sharedPref.getBoolean("isDarkMode", false)) }
        val colorBackground by remember { mutableStateOf (if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)) }
        val colorText by remember { mutableStateOf (if (isDarkMode) Color.White else Color.Black) }
        val colorContainer by remember { mutableStateOf (if (isDarkMode) Color(36, 36, 36) else Color.LightGray) }
        val colorBLue2Yellow by remember { mutableStateOf (if (isDarkMode) Color(255, 228, 0) else Color(0, 27, 255)) }

        Dialog(
            onDismissRequest = { }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colorBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Wymagane połączenie z WiFi",
                        color = colorText,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ta aplikacja wymaga połączenia się z WiFi. Proszę połączyć się z siecią WiFi, aby kontynuować.",
                        color = colorText,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(64.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Przycisk "Włącz WiFi"
                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorContainer,
                                contentColor = colorBLue2Yellow
                            ),
                            modifier = Modifier
                                .border(2.dp, colorText, RoundedCornerShape(24.dp))
                                .background(colorContainer, shape = RoundedCornerShape(24.dp))
                        ) {
                            Text(
                                text = "Włącz WiFi",
                                color = colorBLue2Yellow,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Przycisk "Zamknij aplikację"
                        Button(
                            onClick = {
                                (context as? Activity)?.finish()
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorContainer,
                                contentColor = colorBLue2Yellow
                            ),
                            modifier = Modifier
                                .border(2.dp, colorText, RoundedCornerShape(24.dp))
                                .background(colorContainer, shape = RoundedCornerShape(24.dp))
                        ) {
                            Text(
                                text = "Zamknij aplikację",
                                color = colorBLue2Yellow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Funkcja odświeżania widgetu
fun refreshWidget(context: Context) {
    val intent = Intent(context, AppWidgetReceiver::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, AppWidgetReceiver::class.java))
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    }
    context.sendBroadcast(intent)
}

@SuppressLint("DiscouragedApi")
@ExperimentalMaterial3Api
@Composable
fun MainScreen() {
    // Stan, który kontroluje przełączanie do `ApiScreen`
    var showApiScreen by remember { mutableStateOf(false) }

    if (showApiScreen) {
        // Wyświetlanie ApiScreen, jeśli `showApiScreen` jest prawdą
        ApiScreen(onBack = { showApiScreen = false })
    } else {
        // Zmienna do kontekstu aplikacji
        val context = LocalContext.current

        // Instancja lokalna SharedPreferences
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Zmienna do przechowywania ilości płytek
        var countESP by remember { mutableIntStateOf(sharedPref.getInt("countESP", 1)) }

        // Zmienne motywu jasnego/ciemnego
        var isDarkMode by remember { mutableStateOf(sharedPref.getBoolean("isDarkMode", false)) }
        var colorBackground by remember { mutableStateOf(if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)) }
        var colorText by remember { mutableStateOf(if (isDarkMode) Color.White else Color.Black) }
        var colorContainer by remember { mutableStateOf(if (isDarkMode) Color(36, 36, 36) else Color.LightGray) }
        var colorBLue2Yellow by remember { mutableStateOf(if (isDarkMode) Color(255, 228, 0) else Color(0, 27, 255)) }

        // Tworzenie pamięci stanu dla przewijania
        val scrollState = rememberScrollState()

        // Ikony motywu jasnego/ciemnego
        val lightModeIcon = painterResource(id = R.drawable.moon_icon)
        val darkModeIcon = painterResource(id = R.drawable.sun_icon)

        // Zmiana nazwy komponentu
        val penIcon = painterResource(id = R.drawable.pen_icon)
        var openRenameName0 by remember { mutableStateOf(false) }
        var openRenameName1 by remember { mutableStateOf(false) }
        var openRenameName2 by remember { mutableStateOf(false) }

        // Zmienna do fokusu wpisywanego tekstu
        val focusManager = LocalFocusManager.current
        val interactionSource = remember { MutableInteractionSource() }
        val interactionSource0 = remember { MutableInteractionSource() }
        val interactionSource1 = remember { MutableInteractionSource() }
        val interactionSource2 = remember { MutableInteractionSource() }

        // Zmienne do przechowywania zapytań
        var previousCall0: Call? = null
        var previousCall1: Call? = null
        var previousCall2: Call? = null

        // Zmienne przechowywania czasu
        var timeUpdateYourData0 by remember { mutableStateOf("Brak") }
        var timeUpdateYourData1 by remember { mutableStateOf("Brak") }
        var timeUpdateYourData2 by remember { mutableStateOf("Brak") }

        // Zmienne do przechowywania wyników pobieranych danych
        var temperatureText0 by remember { mutableStateOf("Temperatura: -1.0 °C") }
        var humidityText0 by remember { mutableStateOf("Wilgotność: -1.0 %") }
        var pressureText0 by remember { mutableStateOf("Ciśnienie: -1.0 hPa") }
        var luxText0 by remember { mutableStateOf("Natężenie światła: -1.0 lx") }
        var infoErrorText0 by remember { mutableStateOf("Błędy: Ładowanie danych!") }
        var nameText0 by remember { mutableStateOf("Zmierzone dane 1") }
        var temperatureText1 by remember { mutableStateOf("Temperatura: -1.0 °C") }
        var humidityText1 by remember { mutableStateOf("Wilgotność: -1.0 %") }
        var pressureText1 by remember { mutableStateOf("Ciśnienie: -1.0 hPa") }
        var luxText1 by remember { mutableStateOf("Natężenie światła: -1.0 lx") }
        var infoErrorText1 by remember { mutableStateOf("Błędy: Ładowanie danych!") }
        var nameText1 by remember { mutableStateOf("Zmierzone dane 2") }
        var temperatureText2 by remember { mutableStateOf("Temperatura: -1.0 °C") }
        var humidityText2 by remember { mutableStateOf("Wilgotność: -1.0 %") }
        var pressureText2 by remember { mutableStateOf("Ciśnienie: -1.0 hPa") }
        var luxText2 by remember { mutableStateOf("Natężenie światła: -1.0 lx") }
        var infoErrorText2 by remember { mutableStateOf("Błędy: Ładowanie danych!") }
        var nameText2 by remember { mutableStateOf("Zmierzone dane 3") }

        // Funkcja do pobierania zapisanych danych z SharedPreferences
        fun loadSavedData0() {
            temperatureText0 = sharedPref.getString("temperature0", "Temperatura: -1.0 °C") ?: ""
            humidityText0 = sharedPref.getString("humidity0", "Wilgotność: -1.0 %") ?: ""
            pressureText0 = sharedPref.getString("pressure0", "Ciśnienie: -1.0 hPa") ?: ""
            luxText0 = sharedPref.getString("lux0", "Natężenie światła: -1.0 lx") ?: ""
            timeUpdateYourData0 = sharedPref.getString("timeUpdateYourData0", "Brak") ?: ""
            nameText0 = sharedPref.getString("name0", "Zmierzone dane 1") ?: ""
        }

        fun loadSavedData1() {
            temperatureText1 = sharedPref.getString("temperature1", "Temperatura: -1.0 °C") ?: ""
            humidityText1 = sharedPref.getString("humidity1", "Wilgotność: -1.0 %") ?: ""
            pressureText1 = sharedPref.getString("pressure1", "Ciśnienie: -1.0 hPa") ?: ""
            luxText1 = sharedPref.getString("lux1", "Natężenie światła: -1.0 lx") ?: ""
            timeUpdateYourData1 = sharedPref.getString("timeUpdateYourData1", "Brak") ?: ""
            nameText1 = sharedPref.getString("name1", "Zmierzone dane 2") ?: ""
        }

        fun loadSavedData2() {
            temperatureText2 = sharedPref.getString("temperature2", "Temperatura: -1.0 °C") ?: ""
            humidityText2 = sharedPref.getString("humidity2", "Wilgotność: -1.0 %") ?: ""
            pressureText2 = sharedPref.getString("pressure2", "Ciśnienie: -1.0 hPa") ?: ""
            luxText2 = sharedPref.getString("lux2", "Natężenie światła: -1.0 lx") ?: ""
            timeUpdateYourData2 = sharedPref.getString("timeUpdateYourData2", "Brak") ?: ""
            nameText2 = sharedPref.getString("name2", "Zmierzone dane 3") ?: ""
        }

        // Jednorazowe ładowanie zapisanych danych
        LaunchedEffect(Unit) {
            loadSavedData0()
            loadSavedData1()
            loadSavedData2()
        }

        // Funkcja do zapisywania danych w SharedPreferences
        fun saveDataToPreferences0() {
            with(sharedPref.edit()) {
                putString("temperature0", temperatureText0)
                putString("humidity0", humidityText0)
                putString("pressure0", pressureText0)
                putString("lux0", luxText0)
                putString("timeUpdateYourData0", timeUpdateYourData0)
                apply()
            }
        }

        fun saveDataToPreferences1() {
            with(sharedPref.edit()) {
                putString("temperature1", temperatureText1)
                putString("humidity1", humidityText1)
                putString("pressure1", pressureText1)
                putString("lux1", luxText1)
                putString("timeUpdateYourData1", timeUpdateYourData1)
                apply()
            }
        }

        fun saveDataToPreferences2() {
            with(sharedPref.edit()) {
                putString("temperature2", temperatureText2)
                putString("humidity2", humidityText2)
                putString("pressure2", pressureText2)
                putString("lux2", luxText2)
                putString("timeUpdateYourData2", timeUpdateYourData2)
                apply()
            }
        }

        // Zmienna na adres IP z pamięci lub pusty ciąg, jeśli adres nie istnieje
        var ipInput0 by remember { mutableStateOf(sharedPref.getString("ESP_IP0", "") ?: "") }
        var ipInput1 by remember { mutableStateOf(sharedPref.getString("ESP_IP1", "") ?: "") }
        var ipInput2 by remember { mutableStateOf(sharedPref.getString("ESP_IP2", "") ?: "") }

        // Funkcja do pobierania danych z ESP8266
        fun fetchYourData(id: Int) {
            val ipInput: String
            try {
                // Adres URL i klient HTTP
                ipInput = when (id) {
                    0 -> ipInput0
                    1 -> ipInput1
                    2 -> ipInput2
                    else -> throw IllegalArgumentException("Niepoprawny ID czujnika")
                }
            } catch (e: Exception) {
                return
            }

            // Sprawdzenie, czy adres IP jest pusty
            if (ipInput.isEmpty()) {
                // Ustawienie komunikatu o błędzie, jeśli adres IP nie jest wpisany
                when (id) {
                    0 -> infoErrorText0 = "Błędy: Brak adresu IP!"
                    1 -> infoErrorText1 = "Błędy: Brak adresu IP!"
                    2 -> infoErrorText2 = "Błędy: Brak adresu IP!"
                }
                return
            }

            try {
                // Anulowanie poprzedniego zapytania (jeśli istnieje)
                when (id) {
                    0 -> previousCall0?.cancel()
                    1 -> previousCall1?.cancel()
                    2 -> previousCall2?.cancel()
                }

                val url = if (ipInput.startsWith("http://") || ipInput.startsWith("https://")) ipInput else "http://$ipInput"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val call = client.newCall(request)

                // Zapisanie odniesienia do aktualnego zapytania
                when (id) {
                    0 -> previousCall0 = call
                    1 -> previousCall1 = call
                    2 -> previousCall2 = call
                }

                // Wykonywanie żądania HTTP w tle
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Brak zmiany danych, zachowywane są ostatnie dostępne poprawne dane
                        when (id) {
                            0 -> infoErrorText0 = if (call.isCanceled()) "Błędy: Zbyt długi czas oczekiwania na serwer!" else "Błędy: Błąd połączenia!"
                            1 -> infoErrorText1 = if (call.isCanceled()) "Błędy: Zbyt długi czas oczekiwania na serwer!" else "Błędy: Błąd połączenia!"
                            2 -> infoErrorText2 = if (call.isCanceled()) "Błędy: Zbyt długi czas oczekiwania na serwer!" else "Błędy: Błąd połączenia!"
                        }
                    }

                    @SuppressLint("DefaultLocale")
                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let { jsonResponse ->
                            try {
                                // Parsowanie JSON
                                val json = JSONObject(jsonResponse)

                                // Sprawdzanie poprawności danych
                                val temperature = json.optDouble("temperatureDHT", Double.NaN)
                                val temperatureRounded = BigDecimal(temperature).setScale(1, RoundingMode.HALF_UP).toDouble()
                                val humidity = json.optDouble("humidity", Double.NaN)
                                val humidityRounded = BigDecimal(humidity).setScale(1, RoundingMode.HALF_UP).toDouble()
                                val pressure = json.optDouble("pressure", Double.NaN)
                                val pressureRounded = BigDecimal(pressure).setScale(1, RoundingMode.HALF_UP).toDouble()
                                val lux = json.optDouble("lux", Double.NaN)
                                val luxRounded = BigDecimal(lux).setScale(1, RoundingMode.HALF_UP).toDouble()

                                if (!temperature.isNaN() && !humidity.isNaN() && !pressure.isNaN() && !lux.isNaN()) {
                                    // Aktualizacja danych tylko wtedy, gdy są poprawne
                                    when (id) {
                                        0 -> {
                                            temperatureText0 = "Temperatura: $temperatureRounded °C"
                                            humidityText0 = "Wilgotność: $humidityRounded %"
                                            pressureText0 = "Ciśnienie: $pressureRounded hPa"
                                            luxText0 = "Natężenie światła: $luxRounded lx"
                                            infoErrorText0 = "Błędy: Brak ☺"
                                            timeUpdateYourData0 = getCurrentTime()

                                            // Zapisanie danych w SharedPreferences
                                            saveDataToPreferences0()
                                        }
                                        1 -> {
                                            temperatureText1 = "Temperatura: $temperatureRounded °C"
                                            humidityText1 = "Wilgotność: $humidityRounded %"
                                            pressureText1 = "Ciśnienie: $pressureRounded hPa"
                                            luxText1 = "Natężenie światła: $luxRounded lx"
                                            infoErrorText1 = "Błędy: Brak ☺"
                                            timeUpdateYourData1 = getCurrentTime()

                                            // Zapisanie danych w SharedPreferences
                                            saveDataToPreferences1()
                                        }
                                        2 -> {
                                            temperatureText2 = "Temperatura: $temperatureRounded °C"
                                            humidityText2 = "Wilgotność: $humidityRounded %"
                                            pressureText2 = "Ciśnienie: $pressureRounded hPa"
                                            luxText2 = "Natężenie światła: $luxRounded lx"
                                            infoErrorText2 = "Błędy: Brak ☺"
                                            timeUpdateYourData2 = getCurrentTime()

                                            // Zapisanie danych w SharedPreferences
                                            saveDataToPreferences2()
                                        }
                                    }
                                } else {
                                    // Gdy dane nie poprawne, zachowywane są ostatnie dostępne poprawne dane
                                    when (id) {
                                        0 -> infoErrorText0 = "Błędy: Nieprawidłowe wartości pomiarów!"
                                        1 -> infoErrorText1 = "Błędy: Nieprawidłowe wartości pomiarów!"
                                        2 -> infoErrorText2 = "Błędy: Nieprawidłowe wartości pomiarów!"
                                    }
                                }
                            } catch (e: Exception) {
                                // W przypadku błędu parsowania, zachowywane są ostatnie dostępne poprawne dane
                                when (id) {
                                    0 -> infoErrorText0 = "Błędy: Błąd parsowania danych!"
                                    1 -> infoErrorText1 = "Błędy: Błąd parsowania danych!"
                                    2 -> infoErrorText2 = "Błędy: Błąd parsowania danych!"
                                }
                            }
                        } ?: run {
                            // Brak danych w odpowiedzi, również zachowywane są ostatnie dostępne poprawne dane
                            when (id) {
                                0 -> infoErrorText0 = "Błędy: Pusta odpowiedź serwera!"
                                1 -> infoErrorText1 = "Błędy: Pusta odpowiedź serwera!"
                                2 -> infoErrorText2 = "Błędy: Pusta odpowiedź serwera!"
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                when (id) {
                    0 -> infoErrorText0 = "Błędy: Błędny adres IP!"
                    1 -> infoErrorText1 = "Błędy: Błędny adres IP!"
                    2 -> infoErrorText2 = "Błędy: Błędny adres IP!"
                }
            }
        }

        fun closeRenameName() {
            openRenameName0 = false
            openRenameName1 = false
            openRenameName2 = false
        }

        if (openRenameName0 || openRenameName1 || openRenameName2) {
            Dialog(
                onDismissRequest = { }
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = colorBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Zmień nazwę",
                            color = colorText,
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = if (openRenameName0) {
                                nameText0
                            } else if (openRenameName1) {
                                nameText1
                            } else {
                                nameText2
                            },
                            onValueChange = {
                                if (openRenameName0) {
                                    nameText0 = it
                                } else if (openRenameName1) {
                                    nameText1 = it
                                } else {
                                    nameText2 = it
                                }
                                with(sharedPref.edit()) {
                                    putString("name0", nameText0)
                                    putString("name1", nameText1)
                                    putString("name2", nameText2)
                                    apply()
                                }
                            },
                            maxLines = 1,
                            minLines = 1,
                            singleLine = true,
                            interactionSource = interactionSource,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp + with(LocalDensity.current) { 16.sp.toDp() })
                                .border(
                                    width = 2.dp,
                                    color = colorText,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .background(
                                    colorContainer,
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            shape = RoundedCornerShape(24.dp),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Red,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Red,
                                unfocusedTextColor = colorText,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                selectionColors = LocalTextSelectionColors.current
                            )
                        )

                        Spacer(modifier = Modifier.height(64.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    closeRenameName()
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorContainer,
                                    contentColor = colorBLue2Yellow
                                ),
                                modifier = Modifier
                                    .border(2.dp, colorText, RoundedCornerShape(24.dp))
                                    .background(colorContainer, shape = RoundedCornerShape(24.dp))
                            ) {
                                Text(
                                    text = "OK",
                                    color = colorBLue2Yellow,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun SensorDataDisplay(
            id: Int
        ) {
            val currentIpInput = when (id) {
                0 -> ipInput0
                1 -> ipInput1
                else -> ipInput2
            }

            val interactionSource = when (id) {
                0 -> interactionSource0
                1 -> interactionSource1
                else -> interactionSource2
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(colorText))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                val focusRequester = remember { FocusRequester() }
                var isFocused by remember { mutableStateOf(false) }

                // Pole tekstowe do wprowadzania adresu IP
                OutlinedTextField(
                    value = currentIpInput,
                    onValueChange = { newValue ->
                        when (id) {
                            0 -> {
                                ipInput0 = newValue
                                sharedPref.edit().putString("ESP_IP0", newValue).apply()
                            }
                            1 -> {
                                ipInput1 = newValue
                                sharedPref.edit().putString("ESP_IP1", newValue).apply()
                            }
                            else -> {
                                ipInput2 = newValue
                                sharedPref.edit().putString("ESP_IP2", newValue).apply()
                            }
                        }
                    },
                    maxLines = 1,
                    minLines = 1,
                    singleLine = true,
                    placeholder = {
                        if (!isFocused) {
                            Text(
                                text = "Wprowadź adres IP...",
                                color = colorBLue2Yellow,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp + with(LocalDensity.current) { 16.sp.toDp() })
                        .border(
                            width = 2.dp,
                            color = if (isFocused) Color.Red else colorText,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            colorContainer,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    shape = RoundedCornerShape(24.dp),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Red,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Red,
                        unfocusedTextColor = colorText,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        selectionColors = LocalTextSelectionColors.current
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "     ${
                            when (id) {
                                0 -> nameText0
                                1 -> nameText1
                                else -> nameText2
                            }
                        }",
                        color = colorText,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(12f)
                    )

                    Icon(
                        painter = penIcon,
                        contentDescription = "Przełącz motyw",
                        tint = colorText,
                        modifier = Modifier
                            .size(28.dp, 28.dp)
                            .weight(1f)
                            .clickable {
                                when (id) {
                                    0 -> openRenameName0 = true
                                    1 -> openRenameName1 = true
                                    else -> openRenameName2 = true
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ostatnia aktualizacja:\n${
                        when (id) {
                            0 -> timeUpdateYourData0
                            1 -> timeUpdateYourData1
                            else -> timeUpdateYourData2
                        }
                    }",
                    color = colorText,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Wyświetlanie danych uzyskanych z serwera
                Text(
                    text = when (id) {
                        0 -> temperatureText0
                        1 -> temperatureText1
                        else -> temperatureText2
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = colorText,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (id) {
                        0 -> humidityText0
                        1 -> humidityText1
                        else -> humidityText2
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = colorText,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (id) {
                        0 -> pressureText0
                        1 -> pressureText1
                        else -> pressureText2
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = colorText,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (id) {
                        0 -> luxText0
                        1 -> luxText1
                        else -> luxText2
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = colorText,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                val infoError = when (id) {
                    0 -> infoErrorText0
                    1 -> infoErrorText1
                    else -> infoErrorText2
                }

                Text(
                    text = infoError,
                    fontSize = 18.sp,
                    lineHeight = 16.sp,
                    color = if (infoError == "Błędy: Brak ☺") colorText else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
                .background(colorBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Przycisk zmiany ekranu
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) {
                                focusManager.clearFocus()
                                showApiScreen = true
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Pogoda API",
                            tint = colorText,
                            modifier = Modifier
                                .fillMaxSize()
                                .size(48.dp)
                        )
                    }

                    // Przycisk zmiany motywu
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) {
                                focusManager.clearFocus()
                                isDarkMode = !isDarkMode
                                colorBackground =
                                    if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)
                                colorText = if (isDarkMode) Color.White else Color.Black
                                colorContainer =
                                    if (isDarkMode) Color(24, 24, 24) else Color.LightGray
                                colorBLue2Yellow =
                                    if (isDarkMode) Color(255, 228, 0) else Color(0, 27, 255)

                                // Zapis motywu do SharedPreferences
                                sharedPref
                                    .edit()
                                    .putBoolean("isDarkMode", isDarkMode)
                                    .apply()
                            }
                    ) {
                        Icon(
                            painter = if (isDarkMode) darkModeIcon else lightModeIcon,
                            contentDescription = "Przełącz motyw",
                            tint = colorText,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            when (countESP) {
                1 -> {
                    for (id in 0..0) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SensorDataDisplay(id = id)
                    }
                }
                2 -> {
                    for (id in 0..1) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SensorDataDisplay(id = id)
                    }
                }
                3 -> {
                    for (id in 0..2) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SensorDataDisplay(id = id)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(colorText))

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Przycisk dodania/odjęcia pomiarów
                    when (countESP) {
                        1 -> {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(48.dp))
                                    .border(4.dp, colorText, RoundedCornerShape(48.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource
                                    ) {
                                        focusManager.clearFocus()
                                        showApiScreen = true
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Dodaj pomiar",
                                    tint = colorText,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .clickable {
                                            if (countESP < 3) countESP += 1
                                            with(sharedPref.edit()) {
                                                putInt("countESP", countESP)
                                                apply()
                                            }
                                        }
                                )
                            }
                        }
                        2 -> {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(48.dp))
                                    .border(4.dp, colorText, RoundedCornerShape(48.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource
                                    ) {
                                        focusManager.clearFocus()
                                        showApiScreen = true
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Dodaj pomiar",
                                    tint = colorText,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .clickable {
                                            if (countESP < 3) countESP += 1
                                            with(sharedPref.edit()) {
                                                putInt("countESP", countESP)
                                                apply()
                                            }
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.width(24.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(48.dp))
                                    .border(4.dp, colorText, RoundedCornerShape(48.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource
                                    ) {
                                        focusManager.clearFocus()
                                        showApiScreen = true
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Usuń pomiar",
                                    tint = colorText,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                        .clickable {
                                            if (countESP > 1) countESP -= 1
                                            with(sharedPref.edit()) {
                                                putInt("countESP", countESP)
                                                apply()
                                            }
                                        }
                                )
                            }
                        }
                        3 -> {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(48.dp))
                                    .border(4.dp, colorText, RoundedCornerShape(48.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource
                                    ) {
                                        focusManager.clearFocus()
                                        showApiScreen = true
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Usuń pomiar",
                                    tint = colorText,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                        .clickable {
                                            if (countESP > 1) countESP -= 1
                                            with(sharedPref.edit()) {
                                                putInt("countESP", countESP)
                                                apply()
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Wywołanie funkcji w pętli
        LaunchedEffect(Unit) {
            while (true) {
                delay(500L)
                fetchYourData(0)
                fetchYourData(1)
                fetchYourData(2)
                delay(1500L)
                loadSavedData0()
                loadSavedData1()
                loadSavedData2()
                delay(500L)
                refreshWidget(context)
                delay(5000L)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("DiscouragedApi", "MutableCollectionMutableState")
@ExperimentalMaterial3Api
@Composable
fun ApiScreen(onBack: () -> Unit) {
    // Zmienna do kontekstu aplikacji
    val context = LocalContext.current

    // Instancja lokalna SharedPreferences
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Zmienne motywu jasnego/ciemnego
    var isDarkMode by remember { mutableStateOf(sharedPref.getBoolean("isDarkMode", false)) }
    var colorBackground by remember { mutableStateOf(if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)) }
    var colorText by remember { mutableStateOf(if (isDarkMode) Color.White else Color.Black) }
    var colorContainer by remember { mutableStateOf(if (isDarkMode) Color(36, 36, 36) else Color.LightGray) }
    var colorBLue2Yellow by remember { mutableStateOf(if (isDarkMode) Color(255, 228, 0) else Color(0, 27, 255)) }

    // Tworzenie pamięci stanu dla przewijania
    val scrollState = rememberScrollState()
    val scrollCityState = rememberScrollState()

    // Zmienne do przechowywania zapytań
    var previousCallApi: Call? = null

    // Ikony motywu jasnego/ciemnego
    val lightModeIcon = painterResource(id = R.drawable.moon_icon)
    val darkModeIcon = painterResource(id = R.drawable.sun_icon)

    // Zmienna do fokusu wpisywanego tekstu
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    // Zmienne do przechowywania wyników pobieranych danych z API
    var temperatureApiText by remember { mutableStateOf("Temperatura: -1 °C") }
    var humidityApiText by remember { mutableStateOf("Wilgotność: -1 %") }
    var changeRainApiText by remember { mutableStateOf("Opady deszczu: -1 %") }
    var pressureApiText by remember { mutableStateOf("Ciśnienie: -1 hPa") }
    var cityApi by remember { mutableStateOf("Miasto: Rzeszow") }
    var weatherCondition by remember { mutableStateOf("Bezchmurnie") }
    var iconWeatherApi by remember { mutableStateOf("Clear") }
    var infoErrorApiText by remember { mutableStateOf("Błędy: Ładowanie danych!") }
    var infoErrorWeatherApiText by remember { mutableStateOf("Błędy: Ładowanie danych!") }

    // Zmienne do wykresu
    var tempEntries by remember { mutableStateOf(mutableListOf<Pair<Float, Float>>()) }
    var rainEntries by remember { mutableStateOf(mutableListOf<Pair<Float, Float>>()) }

    // Zmienne dla lokalizacji
    var isUsingCurrentLocation by remember { mutableStateOf(false) }
    isUsingCurrentLocation = sharedPref.getBoolean("cityRead", false)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Miasta i pogody do wyboru z API
    val cities = listOf(
        "Twoja lokalizacja",
        "Bialystok",
        "Bydgoszcz",
        "Gdansk",
        "Gdynia",
        "Katowice",
        "Krakow",
        "Lodz",
        "Lublin",
        "Opole",
        "Poznan",
        "Rzeszow",
        "Szczecin",
        "Torun",
        "Warszawa",
        "Wroclaw",
        "Zielona Gora"
    )
    val weatherTranslations = mapOf(
        "Sunny" to "Słonecznie",
        "Clear" to "Bezchmurnie",
        "Partly cloudy" to "Lekkie zachmurzenie",
        "Cloudy" to "Zachmurzenie",
        "Overcast" to "Pełne zachmurzenie",
        "Haze" to "Lekka mgła",
        "Mist" to "Mgła",
        "Fog" to "Mgła",
        "Showers" to "Przelotne opady",
        "Light drizzle" to "Mżawka",
        "Drizzle" to "Mżawka",
        "Heavy drizzle" to "Mżawka",
        "Light freezing drizzle" to "Marznąca mżawka",
        "Freezing drizzle" to "Marznąca mżawka",
        "Heavy freezing drizzle" to "Marznąca mżawka",
        "Light rain" to "Lekki deszcz",
        "Rain" to "Deszcz",
        "Heavy rain" to "Duży deszcz",
        "Patchy rain nearby" to "Deszcz",
        "Light freezing rain" to "Marznący lekki deszcz",
        "Freezing rain" to "Marznący deszcz",
        "Heavy freezing rain" to "Marznący duży deszcz",
        "Thunderstorm" to "Burza",
        "Thundery outbreaks in nearby" to "Burza",
        "Light snow" to "Lekki śnieg",
        "Light snow showers" to "Lekki śnieg",
        "Snow" to "Śnieg",
        "Patchy snow nearby" to "Śnieg",
        "Heavy snow" to "Intensywny śnieg",
        "Heavy snow showers" to "Intensywny śnieg",
        "Blizzard" to "Zamień śnieżna",
        "Hail" to "Grad",
        "Sleet" to "Deszcz ze śniegiem",
        "Patchy sleet nearby" to "Deszcz ze śniegiem",
        "Windy" to "Wietrznie",
        "Tornado" to "Tornado",
        "Hurricane" to "Huragan"
    )
    var expanded by remember { mutableStateOf(false) }
    var cityGet = sharedPref.getString("cityApi", "") ?: ""
    cityGet = if (cityGet.isNotEmpty()) {
        cityGet.substring(8)
    } else {
        "Rzeszow" // Domyślne miasto
    }
    var city by remember { mutableStateOf(cityGet) }
    val cityRead = if (isUsingCurrentLocation) {
        "Twoja lokalizacja"
    } else {
        city
    }

    // Zmienna przechowywania czasu
    var timeUpdateApiData by remember { mutableStateOf("Brak") }

    // Funkcja do pobierania zapisanych danych z SharedPreferences
    fun loadSavedData() {
        temperatureApiText = sharedPref.getString("temperatureApi", "Temperatura: -1 °C") ?: ""
        humidityApiText = sharedPref.getString("humidityApi", "Wilgotność: -1 %") ?: ""
        changeRainApiText = sharedPref.getString("changeRainApi", "Opady deszczu: -1 %") ?: ""
        pressureApiText = sharedPref.getString("pressureApi", "Ciśnienie: -1 hPa") ?: ""
        cityApi = sharedPref.getString("cityApi", "Miasto: Rzeszow") ?: ""
        weatherCondition = sharedPref.getString("weatherConditionApi", "Bezchmurnie") ?: ""
        iconWeatherApi = sharedPref.getString("iconWeatherApi", "Clear") ?: ""
        timeUpdateApiData = sharedPref.getString("lastUpdateApiTime", "Brak") ?: ""

        val gson = Gson()
        val tempEntriesJson = sharedPref.getString("tempEntriesApi", null)
        val rainEntriesJson = sharedPref.getString("rainEntriesApi", null)
        val type = object : TypeToken<List<Map<String, Float>>>() {}.type
        val tempEntriesMap: List<Map<String, Float>> = if (tempEntriesJson != null) { gson.fromJson(tempEntriesJson, type) } else { emptyList() }
        val rainEntriesMap: List<Map<String, Float>> = if (rainEntriesJson != null) { gson.fromJson(rainEntriesJson, type) } else { emptyList() }
        tempEntries = tempEntriesMap.map { Pair(it["x"] ?: 0f, it["y"] ?: 0f) }.toMutableList()
        rainEntries = rainEntriesMap.map { Pair(it["x"] ?: 0f, it["y"] ?: 0f) }.toMutableList()
    }

    // Jednorazowe ładowanie zapisanych danych
    LaunchedEffect(Unit) {
        loadSavedData()
    }

    // Funkcja do zapisywania danych w SharedPreferences
    fun saveApiDataToPreferences() {
        with(sharedPref.edit()) {
            putString("temperatureApi", temperatureApiText)
            putString("humidityApi", humidityApiText)
            putString("changeRainApi", changeRainApiText)
            putString("pressureApi", pressureApiText)
            putString("cityApi", cityApi)
            putString("weatherConditionApi", weatherCondition)
            putString("lastUpdateApiTime", timeUpdateApiData)
            putString("iconWeatherApi", iconWeatherApi)
            apply()
        }
    }

    fun saveEntriesToPreferences() {
        with(sharedPref.edit()) {
            val gson = Gson()
            val tempEntriesMap = tempEntries.map { mapOf("x" to it.first, "y" to it.second) }
            val rainEntriesMap = rainEntries.map { mapOf("x" to it.first, "y" to it.second) }
            val tempEntriesJson = gson.toJson(tempEntriesMap)
            val rainEntriesJson = gson.toJson(rainEntriesMap)
            putString("tempEntriesApi", tempEntriesJson)
            putString("rainEntriesApi", rainEntriesJson)
            apply()
        }
    }

    // Funkcja do pobierania danych z API
    fun fetchApiData() {
        try {
            // Anulowanie poprzedniego zapytania (jeśli istnieje)
            previousCallApi?.cancel()

            // Adres URL i klient HTTP
            val url = "https://wttr.in/$city?format=j1"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val call = client.newCall(request)

            // Zapisanie odniesienia do aktualnego zapytania
            previousCallApi = call

            // Wykonywanie żądania HTTP w tle
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Brak zmiany danych, zachowywane są ostatnie dostępne poprawne dane
                    infoErrorApiText = if (call.isCanceled()) {
                        "Błędy: Zapytanie anulowane"
                    } else {
                        "Błędy: Błąd połączenia!"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonResponse = response.body?.string()
                    if (jsonResponse == "This query is already being processed") {
                        infoErrorApiText =
                            "Błędy: Zapytanie zostało wysłane zbyt dużo razy! Spróbuj ponownie później."
                        return
                    }
                    if (jsonResponse != null) {
                        try {
                            // Parsowanie JSON
                            val json = JSONObject(jsonResponse)
                            val currentCondition = json.getJSONArray("current_condition").getJSONObject(0)
                            val weather = json.getJSONArray("weather").getJSONObject(0)

                            // Sprawdzanie poprawności danych
                            val temperature = currentCondition.optDouble("temp_C", -1001.0).toInt()
                            val humidity = currentCondition.optDouble("humidity", -1001.0).toInt()
                            val changeRainFull = weather.getJSONArray("hourly").getJSONObject(0).getString("chanceofrain")
                            val changeRain = try { changeRainFull.toInt() } catch (e: NumberFormatException) { -1001 }
                            val pressure = currentCondition.optDouble("pressure", -1001.0).toInt()
                            val conditionFull = currentCondition.getJSONArray("weatherDesc").getJSONObject(0).getString("value")
                            val condition = conditionFull.substringBefore(",")
                            val translatedCondition = weatherTranslations[condition] ?: condition

                            if (temperature > -1000 && changeRain > -1000 && humidity > -1000 && pressure > -1000 && condition.isNotEmpty()) {
                                // Aktualizacja danych tylko wtedy, gdy są poprawne
                                temperatureApiText = "Temperatura: $temperature °C"
                                humidityApiText = "Wilgotność: $humidity %"
                                changeRainApiText = "Opady deszczu: $changeRain %"
                                pressureApiText = "Ciśnienie: $pressure hPa"
                                infoErrorApiText = "Błędy: Brak ☺"
                                weatherCondition = translatedCondition
                                iconWeatherApi = condition
                                cityApi = "Miasto: $city"
                                timeUpdateApiData = getCurrentTime()

                                // Zapisanie danych w SharedPreferences
                                saveApiDataToPreferences()
                            } else {
                                // Gdy dane nie poprawne, zachowywane są ostatnie dostępne poprawne dane
                                infoErrorApiText = "Błędy: Nieprawidłowe wartości pomiarów!"
                            }
                        } catch (e: Exception) {
                            // W przypadku błędu parsowania, zachowywane są ostatnie dostępne poprawne dane
                            infoErrorApiText = "Błędy: Błąd parsowania danych!"
                        }

                        try {
                            // Parsowanie JSON
                            val json = JSONObject(jsonResponse)
                            val weatherArray = json.getJSONArray("weather")

                            if (weatherArray.length() > 0) {
                                tempEntries = mutableListOf()
                                rainEntries = mutableListOf()

                                // Ustalenie bieżącego czasu i najbliższego zapisu co 3 godziny
                                val currentTime = Calendar.getInstance().apply {
                                    if (get(Calendar.MINUTE) > 0) {
                                        add(Calendar.HOUR_OF_DAY, 1) // Przejdź do najbliższej pełnej godziny pomiaru
                                    }
                                    if (get(Calendar.HOUR_OF_DAY) % 3 != 0) {
                                        add(Calendar.HOUR_OF_DAY, 1) // Przejdź do najbliższej pełnej 3 godziny pomiaru
                                    }
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }

                                // Przetwarzanie danych, zaczynając od najbliższej pełnej godziny
                                var entriesAdded = 0

                                outerLoop@ for (i in 0 until weatherArray.length()) {
                                    val dayForecast = weatherArray.getJSONObject(i)
                                    val hourlyArray = dayForecast.getJSONArray("hourly")

                                    // Pętla iterująca po godzinach prognozy w danym dniu
                                    for (j in 0 until hourlyArray.length()) {
                                        if (entriesAdded >= 9) break@outerLoop // Zatrzymanię pętli, gdy uzyskano 9 prognoz

                                        val hourForecast = hourlyArray.getJSONObject(j)
                                        val timeString = hourForecast.optString("time", null.toString())

                                        // Tworzenie pełnej daty i godziny na podstawie `time`
                                        val forecastTime = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
                                            set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
                                            set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
                                            set(Calendar.HOUR_OF_DAY, timeString.toInt() / 100)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }

                                        // Sprawdzenie, czy prognoza jest dla obecnej godziny lub późniejsza
                                        if (forecastTime.timeInMillis >= currentTime.timeInMillis) {
                                            val avgTemp = hourForecast.optDouble("tempC", Double.NaN)
                                            val rainChance = hourForecast.optDouble("chanceofrain", Double.NaN)

                                            // Dodanie prognozy, jeśli wartości są prawidłowe
                                            if (!avgTemp.isNaN() && !rainChance.isNaN()) {
                                                tempEntries.add(Pair(entriesAdded.toFloat(), avgTemp.toFloat()))
                                                rainEntries.add(Pair(entriesAdded.toFloat(), rainChance.toFloat()))
                                                entriesAdded++
                                            }

                                            // Przesunięcie currentTime o 3 godziny, aby pobrać kolejną prognozę
                                            currentTime.add(Calendar.HOUR_OF_DAY, 3)
                                        }
                                    }

                                    currentTime.add(Calendar.DAY_OF_MONTH, 1) // Przejście na następny dzień prognozy
                                    currentTime.set(Calendar.HOUR_OF_DAY, 0) // Reset godziny na początek następnego dnia
                                }

                                infoErrorWeatherApiText = "Błędy: Brak ☺"

                                // Zapisanie danych odnośnie najbliższych godzin
                                saveEntriesToPreferences()
                            } else {
                                // Gdy dane nie poprawne, zachowywane są ostatnie dostępne poprawne dane
                                infoErrorWeatherApiText = "Błędy: Nieprawidłowe wartości pomiarów!"
                            }
                        } catch (e: Exception) {
                            // W przypadku błędu parsowania, zachowywane są ostatnie dostępne poprawne dane
                            infoErrorWeatherApiText = "Błędy: Błąd parsowania danych!"
                        }
                    } else {
                        // Brak danych w odpowiedzi, również zachowywane są ostatnie dostępne poprawne dane
                        infoErrorApiText = "Błędy: Pusta odpowiedź serwera!"
                    }
                }
            })
        } catch (e: IllegalArgumentException) {
            infoErrorApiText = "Błędy: Nieprawidłowy adres IP!"
        }
        loadSavedData()
    }

    @Composable
    fun LineChartView(
        entries: List<Pair<Float, Float>>,
        modifier: Modifier = Modifier
    ) {
        if (entries.isNotEmpty()) {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

            val currentTime = Calendar.getInstance().apply {
                if (get(Calendar.MINUTE) > 0) {
                    add(Calendar.HOUR_OF_DAY, 1)
                }
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                val hour = get(Calendar.HOUR_OF_DAY)
                set(Calendar.HOUR_OF_DAY, hour + (3 - hour % 3) % 3)
            }

            val dateLabels = entries.indices.map { index ->
                (currentTime.clone() as Calendar)
                    .apply { add(Calendar.HOUR_OF_DAY, index * 3) }
                    .let { Html.fromHtml(timeFormat.format(it.time)).toString()
                }
            }

            AndroidView(
                modifier = modifier,
                factory = { context ->
                    LineChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ) // Wymiary wykresu
                        setBackgroundColor(android.graphics.Color.TRANSPARENT) // Przezroczyste tło
                        description.isEnabled = false // Brak opisu wykresu
                        xAxis.position = XAxis.XAxisPosition.BOTTOM // Oś X na dolnym końcu wykresu
                        xAxis.setDrawGridLines(false) // Brak linii siatki na osi X
                        axisRight.isEnabled = false // Brak prawej osi Y
                        axisLeft.setDrawGridLines(false) // Brak linii siatki na osi Y po lewej stronie
                        legend.isEnabled = true // Legenda wykresu

                        // Brak skalowania i przeciągania wykresu
                        isScaleXEnabled = false
                        isScaleYEnabled = false
                        isDragEnabled = false

                        // Wartości minimalne dla osi X
                        val valMin = minOf(0f, entries.minOf { it.second } - 3)
                        val valMax = maxOf(0f, entries.maxOf { it.second } + 3)

                        // Dodatkowy lewy margines
                        extraLeftOffset = if (valMin > -10) 4f else 0f

                        // Ustawienia osi Y
                        axisLeft.axisMinimum = valMin
                        axisLeft.axisMaximum = valMax

                        // Odstępy na początku i końcu osi X
                        xAxis.spaceMin = 0.5f
                        xAxis.spaceMax = 0.5f
                    }
                },
                update = { lineChart ->
                    // Konwersja listy entries na listę lineEntries
                    val lineEntries = entries.map { (x, y) -> Entry(x, y) }

                    // Tworzenie obiektu, który zawiera dane do wyświetlenia na wykresie
                    val lineDataSet = LineDataSet(lineEntries, "Temperatura (°C)").apply {
                        color = android.graphics.Color.RED
                        lineWidth = 2f
                        setCircleColor(android.graphics.Color.RED)
                        setValueTextColor(colorText.toArgb())
                        valueTextSize = 10f
                        circleHoleColor = colorBackground.toArgb()
                        circleRadius = 4f
                        axisDependency = YAxis.AxisDependency.LEFT

                        // Usunięcie części dziesiętnej
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()
                            }
                        }
                    }

                    // Tworzenie i przypisanie LineData do wykresu
                    lineChart.data = LineData(lineDataSet)

                    // Ustawienie niestandardowego formatera dla osi X, aby wyświetlać daty i godziny
                    lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in dateLabels.indices) dateLabels[index] else ""
                        }
                    }

                    // Ustawienie niestandardowego formatera na osi Y, aby zawsze miała 3-znakowe wartości
                    lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
                        @SuppressLint("DefaultLocale")
                        override fun getFormattedValue(value: Float): String {
                            val intValue = value.toInt().toString()
                            return when (intValue.length) {
                                1 -> "  $intValue"
                                2 -> " $intValue"
                                else -> intValue
                            }
                        }
                    }

                    // Ustawienie koloru oraz większego rozmiaru czcionki dla tekstów na osi X i Y
                    lineChart.xAxis.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                        spaceMin = 0.5f // Odstęp na początku osi X
                        spaceMax = 0.5f // Odstęp na końcu osi X
                    }
                    lineChart.axisLeft.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                        val valMin = minOf(0f, entries.minOf { it.second } - 3)
                        val valMax = maxOf(0f, entries.maxOf { it.second } + 3)
                        axisMinimum = valMin
                        axisMaximum = valMax
                    }
                    lineChart.axisRight.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                    }

                    // Ustawienia tekstu legendy
                    lineChart.legend.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        yEntrySpace = 2f
                        yOffset = 2f
                    }

                    // Odstęp między wykresem a legendą
                    lineChart.extraBottomOffset = 8f

                    // Odświeżenie wykresu
                    lineChart.invalidate()
                }
            )
        }
    }

    @Composable
    fun BarChartView(
        entries: List<Pair<Float, Float>>,
        modifier: Modifier = Modifier
    ) {
        if (entries.isNotEmpty()) {
            AndroidView(
                modifier = modifier,
                factory = { context ->
                    BarChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ) // Wymiary wykresu
                        setBackgroundColor(android.graphics.Color.TRANSPARENT) // Przezroczyste tło
                        description.isEnabled = false // Brak opisu wykresu
                        xAxis.position = XAxis.XAxisPosition.BOTTOM // Oś X na dolnym końcu wykresu
                        xAxis.setDrawGridLines(false) // Brak linii siatki na osi X
                        axisRight.isEnabled = false // Brak prawej osi Y
                        axisLeft.setDrawGridLines(false) // Brak linii siatki na osi Y po lewej stronie
                        axisLeft.setDrawLabels(false) // Brak etykiet na osi Y po lewej stronie
                        axisLeft.setDrawAxisLine(false) // Brak lewej osi Y
                        legend.isEnabled = true // Legenda wykresu

                        // Brak skalowania i przeciągania wykresu
                        isScaleXEnabled = false
                        isScaleYEnabled = false
                        isDragEnabled = false

                        // Dodatkowy lewy margines
                        extraLeftOffset = 26f

                        // Ustawienia osi Y
                        axisLeft.axisMinimum = 0f
                        axisLeft.axisMaximum = 100f

                        // Odstępy na początku i końcu osi X
                        xAxis.spaceMin = 0.5f
                        xAxis.spaceMax = 0.5f
                    }
                },
                update = { barChart ->
                    // Konwersja listy entries na listę lineEntries
                    val barEntries = entries.map { (x, y) -> BarEntry(x, y.toInt().toFloat()) }

                    // Tworzenie obiektu, który zawiera dane do wyświetlenia na wykresie
                    val barDataSet = BarDataSet(barEntries, "Opady deszczu (%)").apply {
                        // color = if (isDarkMode) android.graphics.Color.rgb(0, 189, 255) else android.graphics.Color.rgb(0, 27, 255)
                        color = android.graphics.Color.rgb(0, 189, 255)
                        axisDependency = YAxis.AxisDependency.LEFT
                        valueTextSize = 10f
                        setValueTextColor(colorText.toArgb())

                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()
                            }
                        }
                    }

                    // Tworzenie i przypisanie BarData do wykresu
                    barChart.data = BarData(barDataSet).apply {
                        barWidth = 0.5f
                    }

                    // Ustawienie koloru oraz większego rozmiaru czcionki dla tekstów na osi X i Y
                    barChart.xAxis.apply {
                        setDrawLabels(false) // Usuwanie etykiety na osi X
                        setDrawAxisLine(false) // Usuwanie linii osi X
                        setDrawGridLines(false) // Usuwanie siatki na osi X
                    }
                    barChart.axisLeft.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                    }
                    barChart.axisRight.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                    }

                    // Ustawienia tekstu legendy
                    barChart.legend.apply {
                        textColor = colorText.toArgb()
                        textSize = 12f
                        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        yEntrySpace = 2f
                        yOffset = 2f
                    }

                    // Odstęp między wykresem a legendą
                    barChart.extraBottomOffset = 22f

                    // Odświeżenie wykresu
                    barChart.invalidate()
                }
            )
        }
    }

    @Composable
    fun OverlappingCharts(
        tempEntries: List<Pair<Float, Float>>,
        rainEntries: List<Pair<Float, Float>>,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Wykres słupkowy dla opadów
            BarChartView(
                entries = rainEntries,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

            // Wykres liniowy dla temperatury
            LineChartView(
                entries = tempEntries,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
            .background(colorBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Przycisk zmiany ekranu
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            focusManager.clearFocus()
                            onBack()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Strona główna",
                        tint = colorText,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Przycisk zmiany motywu
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            focusManager.clearFocus()
                            isDarkMode = !isDarkMode
                            colorBackground =
                                if (isDarkMode) Color(48, 48, 48) else Color(232, 232, 232)
                            colorText = if (isDarkMode) Color.White else Color.Black
                            colorContainer = if (isDarkMode) Color(24, 24, 24) else Color.LightGray
                            colorBLue2Yellow =
                                if (isDarkMode) Color(255, 228, 0) else Color(0, 27, 255)

                            // Zapis motywu do SharedPreferences
                            sharedPref
                                .edit()
                                .putBoolean("isDarkMode", isDarkMode)
                                .apply()
                        }
                ) {
                    Icon(
                        painter = if (isDarkMode) darkModeIcon else lightModeIcon,
                        contentDescription = "Przełącz motyw",
                        tint = colorText,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(colorText))
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Prognoza pogody",
                color = colorText,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wybierz miasto: ",
                    color = colorText,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        text = cityRead,
                        color = colorText,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(colorContainer)
                            .border(2.dp, colorText, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .height(375.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(scrollCityState)
                                    .padding(end = 4.dp)
                            ) {
                                cities.forEach { cityName ->
                                    DropdownMenuItem(
                                        onClick = {
                                            if (cityName == "Twoja lokalizacja") {
                                                if (locationPermissionState.status.isGranted) {
                                                    // Gdy uprawnienie jest przyznane, pobierz lokalizację
                                                    isUsingCurrentLocation = true
                                                    with(sharedPref.edit()) {
                                                        putBoolean(
                                                            "cityRead",
                                                            isUsingCurrentLocation
                                                        )
                                                        apply()
                                                    }
                                                    fetchLocationAndSetCity(
                                                        fusedLocationClient,
                                                        context
                                                    ) { locationCity ->
                                                        city = locationCity
                                                        fetchApiData()
                                                    }
                                                    expanded = false
                                                } else if (locationPermissionState.status.shouldShowRationale) {
                                                    // Pokaż systemowy dialog
                                                    showPermissionDialog = true
                                                } else {
                                                    // Pokaż własny dialog
                                                    locationPermissionState.launchPermissionRequest()
                                                }
                                            } else {
                                                isUsingCurrentLocation = false
                                                with(sharedPref.edit()) {
                                                    putBoolean("cityRead", isUsingCurrentLocation)
                                                    apply()
                                                }
                                                city = cityName
                                                expanded = false
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    fetchApiData()
                                                }, 500)
                                            }
                                        },
                                        text = {
                                            // Elementy rozwijanego menu (miasta)
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(
                                                        2.dp,
                                                        colorText,
                                                        RoundedCornerShape(4.dp)
                                                    ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(colorBackground)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = when (cityName) {
                                                                "Twoja lokalizacja" -> Icons.Default.Home
                                                                else -> Icons.Default.LocationOn
                                                            },
                                                            contentDescription = null,
                                                            modifier = Modifier.size(24.dp),
                                                            tint = when {
                                                                isUsingCurrentLocation && cityName == "Twoja lokalizacja" -> Color.Red
                                                                cityName == city -> Color.Red
                                                                else -> colorBLue2Yellow
                                                            }
                                                        )

                                                        Spacer(modifier = Modifier.width(6.dp))

                                                        Text(
                                                            text = cityName,
                                                            color = colorText,
                                                            fontSize = 18.sp,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Medium
                                                        )

                                                        Spacer(modifier = Modifier.width(4.dp))
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            // Własny wskaźnik przewijania
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .align(Alignment.CenterEnd)
                                    .drawBehind {
                                        // Obliczanie wysokości scrolla, aby odpowiadała proporcjonalnie widocznej zawartości
                                        val contentHeight =
                                            scrollCityState.maxValue.toFloat() + size.height
                                        val scrollbarHeight =
                                            size.height * (size.height / contentHeight)

                                        // Obliczanie przesunięcia scrolla na podstawie obecnej pozycji scrolla
                                        val maxScrollOffset = size.height - scrollbarHeight
                                        val scrollbarOffset =
                                            maxScrollOffset * (scrollCityState.value / scrollCityState.maxValue.toFloat())

                                        // Rysowanie prostokąta scrolla
                                        drawRect(
                                            color = if (isDarkMode) Color.LightGray.copy(alpha = 0.5f) else Color.DarkGray.copy(
                                                alpha = 0.5f
                                            ),
                                            topLeft = androidx.compose.ui.geometry.Offset(
                                                0f,
                                                scrollbarOffset.coerceIn(0f, maxScrollOffset)
                                            ),
                                            size = androidx.compose.ui.geometry.Size(
                                                width = size.width,
                                                height = scrollbarHeight
                                            )
                                        )
                                    }
                            )
                        }
                    }

                    // Dialog z informacją i przekierowaniem do ustawień aplikacji
                    if (showPermissionDialog) {
                        Dialog(
                            onDismissRequest = { }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = colorBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Wymagane uprawnienie do lokalizacji",
                                        color = colorText,
                                        fontSize = 24.sp,
                                        lineHeight = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Aby korzystać z tej funkcji, przejdź do ustawień aplikacji i przyznaj uprawnienie do lokalizacji.",
                                        color = colorText,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(64.dp))

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Przycisk "Przejdź do ustawień"
                                        Button(
                                            onClick = {
                                                showPermissionDialog = false
                                                context.startActivity(
                                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        this.data = Uri.fromParts(
                                                            "package",
                                                            context.packageName,
                                                            null
                                                        )
                                                    }
                                                )
                                            },
                                            shape = RoundedCornerShape(24.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorContainer,
                                                contentColor = colorBLue2Yellow
                                            ),
                                            modifier = Modifier
                                                .border(2.dp, colorText, RoundedCornerShape(24.dp))
                                                .background(
                                                    colorContainer,
                                                    shape = RoundedCornerShape(24.dp)
                                                )
                                        ) {
                                            Text(
                                                text = "Przejdź do ustawień",
                                                color = colorBLue2Yellow,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Przycisk "Anuluj"
                                        Button(
                                            onClick = {
                                                showPermissionDialog = false
                                            },
                                            shape = RoundedCornerShape(24.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorContainer,
                                                contentColor = colorBLue2Yellow
                                            ),
                                            modifier = Modifier
                                                .border(2.dp, colorText, RoundedCornerShape(24.dp))
                                                .background(
                                                    colorContainer,
                                                    shape = RoundedCornerShape(24.dp)
                                                )
                                        ) {
                                            Text(
                                                text = "Anuluj",
                                                color = colorBLue2Yellow,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ostatnia aktualizacja:\n$timeUpdateApiData",
                color = colorText,
                fontSize = 18.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = cityApi,
                style = MaterialTheme.typography.titleMedium,
                color = colorText,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(4f)
                ) {
                    // Wyświetlanie danych uzyskanych z API
                    Text(
                        text = temperatureApiText,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorText,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = humidityApiText,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorText,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = changeRainApiText,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorText,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = pressureApiText,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorText,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                ) {
                    // Wyświetlanie danych uzyskanych z API
                    Text(
                        text = weatherCondition,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorText,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val iconResId = when (iconWeatherApi) {
                        "Sunny" -> R.drawable.sunny_icon
                        "Clear" -> R.drawable.clear_icon
                        "Partly cloudy" -> R.drawable.partly_cloudy_icon
                        "Cloudy" -> R.drawable.cloudy_icon
                        "Overcast" -> R.drawable.overcast_icon
                        "Haze" -> R.drawable.haze_icon
                        "Mist" -> R.drawable.fog_icon
                        "Fog" -> R.drawable.fog_icon
                        "Showers" -> R.drawable.showers_icon
                        "Light drizzle" -> R.drawable.drizzle_icon
                        "Drizzle" -> R.drawable.drizzle_icon
                        "Heavy drizzle" -> R.drawable.drizzle_icon
                        "Light freezing drizzle" -> R.drawable.drizzle_freezing_icon
                        "Freezing drizzle" -> R.drawable.drizzle_freezing_icon
                        "Heavy freezing drizzle" -> R.drawable.drizzle_freezing_icon
                        "Light rain" -> R.drawable.light_rain_icon
                        "Rain" -> R.drawable.rain_icon
                        "Heavy rain" -> R.drawable.heavy_rain_icon
                        "Patchy rain nearby" -> R.drawable.rain_icon
                        "Light freezing rain" -> R.drawable.light_freezing_rain_icon
                        "Freezing rain" -> R.drawable.rain_freezing_icon
                        "Heavy freezing rain" -> R.drawable.heavy_freezing_rain_icon
                        "Thunderstorm" -> R.drawable.thunderstorm_icon
                        "Thundery outbreaks in nearby" -> R.drawable.thunderstorm_icon
                        "Light snow" -> R.drawable.light_snow_icon
                        "Light snow showers" -> R.drawable.light_snow_icon
                        "Snow" -> R.drawable.snow_icon
                        "Patchy snow nearby" -> R.drawable.snow_icon
                        "Heavy snow" -> R.drawable.heavy_snow_icon
                        "Heavy snow showers" -> R.drawable.heavy_snow_icon
                        "Blizzard" -> R.drawable.blizzard_icon
                        "Hail" -> R.drawable.hail_icon
                        "Sleet" -> R.drawable.sleet_icon
                        "Patchy sleet nearby" -> R.drawable.sleet_icon
                        "Windy" -> R.drawable.windy_icon
                        "Tornado" -> R.drawable.tornado_icon
                        "Hurricane" -> R.drawable.hurricane_icon
                        else -> R.drawable.question_icon
                    }

                    if (iconResId == R.drawable.question_icon && iconWeatherApi != "" && iconWeatherApi != " ") {
                        Icon(
                            painter = painterResource(id = R.drawable.question_icon),
                            contentDescription = "Obrazek pogody",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            tint = colorText,
                        )
                    } else if (iconWeatherApi != "" && iconWeatherApi != " ") {
                        Box(
                            modifier = Modifier.height(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = iconResId),
                                contentDescription = "Obrazek pogody",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, colorText, RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = infoErrorApiText,
                fontSize = 18.sp,
                lineHeight = 16.sp,
                color = if (infoErrorApiText == "Błędy: Brak ☺") colorText else Color.Red,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(colorText))

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Wykres pogody",
                    color = colorText,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Wyświetlenie wykres
                OverlappingCharts(
                    tempEntries = tempEntries,
                    rainEntries = rainEntries,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = infoErrorWeatherApiText,
                    fontSize = 18.sp,
                    lineHeight = 16.sp,
                    color = if (infoErrorWeatherApiText == "Błędy: Brak ☺") colorText else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Wywołanie funkcji w pętli
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            fetchApiData()
            delay(500L)
            loadSavedData()
            refreshWidget(context)
            delay(10000L)
        }
    }
}

@SuppressLint("MissingPermission")
fun fetchLocationAndSetCity(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onCityFetched: (String) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Nowe API, asynchroniczne
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    val cityName = addresses.getOrNull(0)?.locality ?: "Nieznana lokalizacja"
                    onCityFetched(cityName)
                }
            } else {
                onCityFetched("Nieznana lokalizacja")
            }
        } else {
            onCityFetched("Nieznana lokalizacja")
        }
    }
}

// Funkcja pomocnicza do pobierania bieżącego czasu w formacie "dd-MM-yyyy HH:mm:ss"
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(System.currentTimeMillis())
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MaterialTheme {
        MainScreen()
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, apiLevel = 34)
@Composable
fun PreviewApiScreen() {
    MaterialTheme {
        ApiScreen(onBack = {})
    }
}
