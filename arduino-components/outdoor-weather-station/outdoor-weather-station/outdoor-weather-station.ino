#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <EEPROM.h>
#include <ESP8266HTTPClient.h>
#include <Adafruit_BME280.h>
#include <Vector.h>
#include <TimeLib.h>

String save_url = "http://192.168.1.106:8080/api/weather-reading";
String WEATHER_STATION_NAME = "test-station";
String WiFiName = "Cybermax.pl@MK";
String WiFiPassword = "202305113518";
String WeatherStationPassword = "1234";

struct Reading {
String temperature;
int humidity;
int pressure;
};

Reading readings[10];
int readingsCount = 0;
bool APConnected = false;
HTTPClient http;
int progress = 0;
bool errorOccured = false;
unsigned long pomiarTime = 0;
int sensorStatus = 0;
Adafruit_BME280 bme;
int readingListSize;

void setup() {
  Serial.begin(115200);
  Serial.println("\n\nWemos start");

  initSensors();
  readingListSize = countReadingsSize();
  WiFi.mode(WIFI_STA);
  startWiFiServices();
}


void loop() {
  readValuesFromSensor();

  sendData();
  delay(10000);
}

void sendData() {
  if (!http.begin(save_url)) {
    Serial.println("Failed to connect to server");
    return;
  }

  http.addHeader("Content-Type", "application/json");
  String postData = createDataJson();

  int responseCode = http.POST(postData);
  Serial.println("Response code: " + (String)responseCode);
  if (responseCode == 201) {
    clearReadings();
    readingsCount = 0;
  }
}

void clearReadings() {
  Serial.println("Clearing");
  for (Reading& r : readings) {
    r.temperature = "";
    r.humidity = 0;
    r.pressure = 0;
  }
}

int countReadingsSize() {
  int size = 0;
  for (Reading reading : readings) {
    size++;
  }
  return size;
}

String createDataJson() {
  String postData = "[";
  for (Reading r : readings) {
    if (!isReadingValid(r)) {
      break;
    }
    postData = postData + "{\"temperature\":\"" + r.temperature + "\","
                + "\"humidity\":\"" + r.humidity + "\","
                + "\"pressure\":\"" + r.pressure + "\","
                + "\"created\":\"" + getCurrentTime() + "\","
                + "\"weatherStationPassword\":\"" + WeatherStationPassword + "\","
                + "\"weatherStationName\":\"" + WEATHER_STATION_NAME + "\"},\n";
  }
  postData = postData.substring(0, postData.length() - 2);
  postData = postData + "]";
  Serial.println(postData);
  return postData;
}

boolean isReadingValid(Reading reading) {
  if (!reading.temperature.equals("") || reading.humidity != 0 || reading.pressure != 0) {
    return true;
  }
  return false;
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
  WiFi.begin(WiFiName, WiFiPassword);

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
    readings[readingsCount].temperature = (String)bme.readTemperature();
    readings[readingsCount].humidity = bme.readHumidity();
    readings[readingsCount].pressure = bme.readPressure();
    if (readingsCount < readingListSize) {
      readingsCount++;
    } else {
      readingsCount = 0;
    }
 }

 String getCurrentTime() {
  time_t currentTime = now();
  char timeString[20];
  sprintf(timeString, "%04d-%02d-%02dT%02d:%02d:%02d",
          year(currentTime), month(currentTime), day(currentTime),
          hour(currentTime), minute(currentTime), second(currentTime));
  return String(timeString);
}


