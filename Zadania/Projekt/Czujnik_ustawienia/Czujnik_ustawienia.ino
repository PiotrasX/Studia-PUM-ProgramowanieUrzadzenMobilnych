#include <WiFi.h>
#include <WebServer.h>
#include <DHT.h>

// Biblioteka do obsługi I2C
#include <Wire.h>

// Biblioteka do czujnika BMP180
#include <Adafruit_BMP085.h>     // BMP180/BMP085

// Biblioteka do czujnika BH1750
#include <BH1750.h>              // BH1750

// Definicje pinów dla czujników
#define DHTPIN 21       // GPIO2 for DHT11
#define DHTTYPE DHT11  // Typ czujnika DHT11

// Definicje pinów dla pierwszej magistrali I2C (BMP180)
#define SDA_PIN 21  // GPIO21 for BMP180
#define SCL_PIN 22  // GPIO22 for BMP180

// Definicje pinów dla drugiej magistrali I2C (BH1750)
#define SDA_PIN_BH1750 19  // GPIO19 for BH1750
#define SCL_PIN_BH1750 18  // GPIO18 for BH1750

// Adres BH1750 (domyślnie 0x23 lub 0x5C)
#define BH1750_ADDRESS 0x23 // Sprawdź adres czujnika

const char* ssid = "TP-Link_2910";      // Zastąp swoją nazwą sieci Wi-Fi
const char* password = "123456789!";    // Zastąp swoim hasłem Wi-Fi

WebServer server(80);
DHT dht(DHTPIN, DHTTYPE);

// Obiekt dla czujnika BMP180
Adafruit_BMP085 bmp;

// Obiekt dla czujnika BH1750
BH1750 lightMeter;

void setup() {
  // Inicjalizacja portu szeregowego
  Serial.begin(115200);
  dht.begin();

  // Inicjalizacja pierwszej magistrali I2C (Wire) dla BMP180
  Wire.begin(SDA_PIN, SCL_PIN); // SDA, SCL for BMP180

  // Inicjalizacja BMP180
  if (!bmp.begin()) {
    Serial.println("Nie można zainicjalizować BMP180");
    while (1);
  }

  // Inicjalizacja drugiej magistrali I2C (Wire1) dla BH1750
  Wire1.begin(SDA_PIN_BH1750, SCL_PIN_BH1750); // SDA, SCL for BH1750

  // Inicjalizacja BH1750 z użyciem Wire1
  if (!lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE, BH1750_ADDRESS, &Wire1)) {
    Serial.println("Nie można zainicjalizować BH1750");
    while (1);
  }

  // Łączenie z Wi-Fi
  WiFi.begin(ssid, password);
  Serial.print("Łączenie z Wi-Fi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println(" Połączono!");
  Serial.print("Adres IP: ");
  Serial.println(WiFi.localIP());

  // Konfiguracja serwera
  server.on("/", handleRoot);
  server.begin();
  Serial.println("Serwer HTTP uruchomiony");
}

void handleRoot() {
  // Odczyt danych z DHT11
  float temperatureDHT = dht.readTemperature();
  float humidity = dht.readHumidity();

  // Odczyt danych z BMP180
  float temperatureBMP = bmp.readTemperature();
  float pressure = bmp.readPressure() / 100.0; // ciśnienie w hPa

  // Odczyt danych z BH1750
  float lux = lightMeter.readLightLevel();

  // Tworzenie odpowiedzi JSON
  String json = "{";
  json += "\"temperatureDHT\":" + String(temperatureDHT) + ",";
  json += "\"humidity\":" + String(humidity) + ",";
  json += "\"temperatureBMP\":" + String(temperatureBMP) + ",";
  json += "\"pressure\":" + String(pressure) + ",";
  json += "\"lux\":" + String(lux);
  json += "}";

  server.send(200, "application/json", json);
}

void loop() {
  server.handleClient();

  // Odczyt danych z czujników
  float temperatureDHT = dht.readTemperature();
  float humidity = dht.readHumidity();
  float temperatureBMP = bmp.readTemperature();
  float pressure = bmp.readPressure() / 100.0; // ciśnienie w hPa
  float lux = lightMeter.readLightLevel();

  // Wyświetlanie danych w monitorze portu szeregowego
  Serial.println("==================================");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
  Serial.print("Temp DHT: ");
  Serial.print(temperatureDHT);
  Serial.println(" C");
  Serial.print("Wilg: ");
  Serial.print(humidity);
  Serial.println(" %");
  Serial.print("Temp BMP: ");
  Serial.print(temperatureBMP);
  Serial.println(" C");
  Serial.print("Cisn: ");
  Serial.print(pressure);
  Serial.println(" hPa");
  Serial.print("Swiatlo: ");
  Serial.print(lux);
  Serial.println(" lx");
  Serial.println("==================================");

  delay(2000); // Odświeżanie co 2 sekundy
}