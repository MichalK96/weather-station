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
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <cstdio>
#include <BH1750FVI.h>

const String host = "192.168.1.106";
const String WEATHER_STATION_NAME = "na-zewnatrz-osrodek";
const String WiFiName = "Cybermax.pl@MK";
const String WiFiPassword = "202305113518";
const String WeatherStationPassword = "1234";


struct Reading {
String temperature;
int humidity;
int pressure;
String created;
String lightIntensity;
};

BH1750FVI LightSensor(BH1750FVI::k_DevModeContLowRes);
const int refreshTimeSec = 180;
Reading readings[250]; //250
int readingsCount = 0;
bool APConnected = false;
HTTPClient http;
int progress = 0;
bool errorOccured = false;
unsigned long pomiarTime = 0;
int sensorStatus = 0;
Adafruit_BME280 bme;
int readingListSize;
uint64_t  dateMillis;
char buffer[21];

void setup() {
  Serial.begin(115200);
  Serial.println("\n\nWemos start");
  readingListSize = countReadingsSize();

  initSensors();
  WiFi.mode(WIFI_STA);
  startWiFiServices();
}


void loop() {
  fetchCurrentMillis();
  readValuesFromSensor();
  sendData();
  
  delay(refreshTimeSec * 1000);
}

void sendData() {
  if (!http.begin("http://" + host + ":8080/api/weather-reading")) {
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
  int count = 0;
  for (Reading r : readings) {
    if (!isReadingValid(r)) {
      break;
    }
    postData = postData + "{\"temperature\":\"" + r.temperature + "\","
                + "\"humidity\":\"" + r.humidity + "\","
                + "\"pressure\":\"" + r.pressure + "\","
                + "\"createdMillis\":\"" + r.created + "\","
                + "\"lightIntensity\":\"" + r.lightIntensity + "\","
                + "\"weatherStationPassword\":\"" + "1234" + "\","
                + "\"weatherStationName\":\"" + WEATHER_STATION_NAME + "\"},\n";
    Serial.println(postData);
  }
  postData = postData.substring(0, postData.length() - 2);
  postData = postData + "]";
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
  LightSensor.begin();

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
    readings[readingsCount].created = getCurrentMillis();
    readings[readingsCount].lightIntensity = LightSensor.GetLightIntensity();
  Serial.print("Light: " + readings[readingsCount].lightIntensity);
    if (readingsCount < readingListSize) {
      readingsCount++;
    } else {
      readingsCount = 0;
    }
 }

  String getCurrentMillis() {
  long long currentTime = dateMillis + millis();
  if (currentTime < 1710624653298ULL) {
    return "";
  }
  snprintf(buffer, sizeof(buffer), "%lld", currentTime);
  return buffer;
}

void fetchCurrentMillis() {
  http.begin("http://" + host + ":8080/api/weather-reading/current-time");
  int responseCode = http.GET();
  if (responseCode == 200) {
    String payload = http.getString();
    dateMillis = toLongLong(payload) - millis();
  } else {
    Serial.println("Error when fetch millis: " + responseCode);
  }
}

long long toLongLong(String value) {
  long long result = 0;
  for (int i = 0; i < value.length(); i++) {
    char c = value.charAt(i);
    if (c < '0' || c > '9') break;
    result *= 10;
    result += (c - '0');
  }
  return result;
}


