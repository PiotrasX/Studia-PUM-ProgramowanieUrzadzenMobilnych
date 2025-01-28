package com.example.testdht

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.GlanceModifier
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.text.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Instancja lokalna SharedPreferences
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Zmienne motywu jasnego/ciemnego
        val isDarkMode = sharedPref.getBoolean("isDarkMode", false)
        val colorBackground = if (isDarkMode) Color(48, 48, 48, 232) else Color(232, 232, 232, 232)
        val colorText = if (isDarkMode) Color.White else Color.Black

        // Zmienne do danych z API
        val temperatureApiText = sharedPref.getString("temperatureApi", "Temperatura: -1 °C") ?: ""
        val humidityApiText = sharedPref.getString("humidityApi", "Wilgotność: -1 %") ?: ""
        val changeRainApiText = sharedPref.getString("changeRainApi", "Opady deszczu: -1 %") ?: ""
        val pressureApiText = sharedPref.getString("pressureApi", "Ciśnienie: -1 hPa") ?: ""
        val cityApi = sharedPref.getString("cityApi", "Miasto: Rzeszow") ?: ""
        val timeUpdateApiData = sharedPref.getString("lastUpdateApiTime", "Brak") ?: ""

        provideContent {
            AppWidgetContent(
                colorBackground = colorBackground,
                colorText = colorText,
                temperatureText = temperatureApiText,
                humidityText = humidityApiText,
                changeRainText = changeRainApiText,
                pressureText = pressureApiText,
                city = cityApi,
                timeUpdateData = timeUpdateApiData
            )
        }
    }

    @SuppressLint("ResourceType")
    @Composable
    fun AppWidgetContent(
        colorBackground: Color,
        colorText: Color,
        temperatureText: String,
        humidityText: String,
        changeRainText: String,
        pressureText: String,
        city: String,
        timeUpdateData: String
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(colorBackground))
                .padding(12.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "Aktualne dane pogodowe",
                style = TextStyle(
                    color = ColorProvider(colorText),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            Text(
                text = "Ostatnia aktualizacja:\n$timeUpdateData",
                style = TextStyle(
                    color = ColorProvider(colorText),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = GlanceModifier.height(20.dp))

            Text(
                text = city,
                style = TextStyle(
                    color = ColorProvider(colorText),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Row {
                Column(
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
                    Text(
                        text = temperatureText,
                        style = TextStyle(
                            color = ColorProvider(colorText),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = humidityText,
                        style = TextStyle(
                            color = ColorProvider(colorText),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = changeRainText,
                        style = TextStyle(
                            color = ColorProvider(colorText),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = pressureText,
                        style = TextStyle(
                            color = ColorProvider(colorText),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

// Odbiornik widgetu
class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Log.d("AppWidgetReceiver", "Otrzymano żądanie odświeżenia widżetu")

            CoroutineScope(Dispatchers.IO).launch {
                glanceAppWidget.updateAll(context)
            }
        }
    }
}
