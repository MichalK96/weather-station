#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <EEPROM.h>
#include <ESP8266HTTPClient.h>
#include <Adafruit_BME280.h>

String save_url = "http://host:8080/api/weather-reading";
String WEATHER_STATION_NAME = "test-station";

bool APConnected = false;
HTTPClient http;
int progress = 0;
bool errorOccured = false;
unsigned long pomiarTime = 0;
int sensorStatus = 0;
float temperature;
int humidity;
int pressure;
Adafruit_BME280 bme;


void setup() {
  Serial.begin(9600);
  Serial.println("\n\nWemos start");

  WiFi.mode(WIFI_STA);
  startWiFiServices();
  initSensors();
}


void loop() {
  readValuesFromSensor();
  Serial.println((String)temperature);
  Serial.println(humidity);
  Serial.println(pressure);

  sendData();
  delay(10000);
}

void sendData() {
  if (!http.begin(save_url)) {
    Serial.println("Failed to connect to server");
    return;
  }

  http.addHeader("Content-Type", "application/json");
  String postData = "{\"temperature\":\"" + (String)temperature + "\","
                + "\"humidity\":\"" + humidity + "\","
                + "\"pressure\":\"" + pressure + "\","
                + "\"weatherStationName\":\"" + WEATHER_STATION_NAME + "\"}";

  http.POST(postData);
  Serial.println("Request sent");
}

void loadSettings(void *data_dest, size_t size) {
  EEPROM.begin(size * 2);
  for(size_t i = 0; i < size; i++) {
    char data = EEPROM.read(i);
    ((char *)data_dest)[i] = data;
  }
}

void startWiFiServices() {
  Serial.println("Connecting with WiFi\n");
  int countWifi = 0;
  WiFi.begin("name", "pass");

  while (WiFi.status() != WL_CONNECTED) {
    delay(330);
    Serial.print(".");
    countWifi+=1;
    if (countWifi>60) {
      break;
    }
  }

    if (WiFi.status() == WL_CONNECTED) {
    APConnected = true;  
    Serial.println("WiFi connected");  
  } else {
    Serial.print("WiFi NOT CONNECTED");
    errorOccured = true;
  }
}

 void initSensors() {
  if (!bme.begin(0x76)) {
    Serial.println("Sensor BME280 not found");
    return;
  }
  Serial.println("Sensor ready");
 }

  void readValuesFromSensor() {
    temperature = bme.readTemperature();
    humidity = bme.readHumidity();
    pressure = bme.readPressure();
 }


