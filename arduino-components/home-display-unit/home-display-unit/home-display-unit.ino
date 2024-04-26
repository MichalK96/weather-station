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
#include <ArduinoJson.h>
#include <ArtronShop_SHT3x.h>
#include "lcd/LcdManager.h"


const String host = "host";
const String WEATHER_STATION_NAME = "name";
const String WiFiName = "name";
const String WiFiPassword = "pass";
const String weatherStationPassword = "pass";


struct Reading {
String temperature;
int humidity;
int pressure;
String created;
String lightIntensity;
int apiResponseCode;
};

int weatherStationRefreshTimeSec = 300;
int homeDisplayRefreshTimeSec = 60;
int pressureOffset = 0;
float tempOffset = 0;
int humidityOffset = 0;
int lcdBrightness = 100;

LcdManager lcd;
ArtronShop_SHT3x sht3x(0x44, &Wire);
BH1750FVI LightSensor(BH1750FVI::k_DevModeContLowRes);
Reading readings[250];
int readingsCount = 0;
bool APConnected = false;
HTTPClient http;
int progress = 0;
int sensorStatus = 0;

int readingListSize;
uint64_t  timestamp;
char buffer[21];

void setup() {
  Serial.begin(115200);
  Serial.println("\n\nWemos start");
  readingListSize = countReadingsSize();
  lcd.init();

  initSensors();
  WiFi.mode(WIFI_STA);
  startWiFiServices();
  lcd.displayPictograms();
  readValuesFromSensor();
  readingsCount--;
}

void loop() {
  fetchSettings();
  readValuesFromSensor();
  refreshDisplayData();
  sendData();

  delay(weatherStationRefreshTimeSec * 1000);
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
  } else {
      readings[readingsCount].apiResponseCode = responseCode;
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
  for (Reading r : readings) {
    if (!isReadingValid(r)) {
      break;
    }
    postData = postData + "{\"temperature\":\"" + r.temperature + "\","
                + "\"humidity\":\"" + r.humidity + "\","
                + "\"pressure\":\"" + r.pressure + "\","
                + "\"createdMillis\":\"" + r.created + "\","
                + "\"lightIntensity\":\"" + r.lightIntensity + "\","
                + "\"apiResponseCode\":\"" + r.apiResponseCode + "\","
                + "\"weatherStationPassword\":\"" + weatherStationPassword + "\","
                + "\"weatherStationName\":\"" + WEATHER_STATION_NAME + "\"},\n";
    // Serial.println(postData);
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
  lcd.print("Laczenie z WiFi", 0, 0);
  int countWifi = 0;
  WiFi.begin(WiFiName, WiFiPassword);

  lcd.setCursor(0, 1);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);   //330
    if (countWifi == 20) {
      lcd.setCursor(0, 2);
    } else if (countWifi == 40) {
      lcd.setCursor(0, 3);
    }
    Serial.print(".");
    lcd.print(".");
    countWifi += 1;
    if (countWifi>60) {
      break;
    }
  }

  if (WiFi.status() == WL_CONNECTED) {
    APConnected = true;  
    Serial.println("WiFi connected");  
    lcd.clear();
    lcd.print("WiFi OK", 6, 1);
    delay(4000);
    lcd.clear();
  } else {
    Serial.print("WiFi NOT CONNECTED");
    lcd.clear();
    lcd.print("PROBLEM Z WiFi", 0, 0);
    restartAfterTimeSec(60);
  }
}

void restartAfterTimeSec(int time) {
  while (true) {
    lcd.print("Za " + (String) time + "s nastapi reset", 0, 2);
    delay(1000);
    time--;
    if (time == 0) {
      lcd.clear();
      ESP.restart();
    }
  }
  
}

 void initSensors() {
  LightSensor.begin();

  // if (!bme.begin(0x76)) {
  //   Serial.println("Sensor BME280 not found");
  //   return;
  // }

  Wire.begin();
  while (!sht3x.begin()) {
    Serial.println("SHT3x not found !");
    delay(1000);
  }

  Serial.println("Sensor ready");
 }

  void readValuesFromSensor() {
    readings[readingsCount].temperature = (String)(sht3x.temperature() + tempOffset);
    readings[readingsCount].humidity = sht3x.humidity() + humidityOffset;
    readings[readingsCount].created = getCurrentMillis();
    readings[readingsCount].lightIntensity = LightSensor.GetLightIntensity();
    if (readingsCount < readingListSize) {
      readingsCount++;
    } else {
      readingsCount = 0;
    }

    if (sht3x.measure()) {
    Serial.print("Temperature: ");
    Serial.print(sht3x.temperature(), 1);
    Serial.print(" *C\tHumidity: ");
    Serial.print(sht3x.humidity(), 1);
    Serial.print(" %RH");
    Serial.println();
  } else {
    Serial.println("SHT3x read error");
  }

 }

  String getCurrentMillis() {
  long long currentTime = timestamp + millis();
  if (currentTime < 1710624653298ULL) {
    return "";
  }
  snprintf(buffer, sizeof(buffer), "%lld", currentTime);
  return buffer;
}

void fetchSettings() {
  fetchTimestamp();
  fetchAndUpdateWeatherStationSettings();
  fetchAndUpdateHomeDisplaySettings();
}

void fetchAndUpdateHomeDisplaySettings() {
  http.begin("http://" + host + ":8080/api/home-display/" + WEATHER_STATION_NAME);
  Serial.println("\nhttp://" + host + ":8080/api/home-display/" + WEATHER_STATION_NAME);
  int responseCode = http.GET();
  if (responseCode == 200) {
    String payload = http.getString();
    updateHomeUnitSettings(payload);
  } else {
    Serial.println("Error when fetch millis: " + responseCode);
  }
}

void fetchAndUpdateWeatherStationSettings() {
  http.begin("http://" + host + ":8080/api/weather-station/" + WEATHER_STATION_NAME);
  int responseCode = http.GET();
  if (responseCode == 200) {
    String payload = http.getString();
    updateStationSettings(payload);
  } else {
    Serial.println("Error when fetch millis: " + responseCode);
  }
}

void updateHomeUnitSettings(String json) {
  const size_t bufferSize = JSON_OBJECT_SIZE(5);
  DynamicJsonDocument jsonBuffer(bufferSize);
  DeserializationError error = deserializeJson(jsonBuffer, json);

  if (error) {
    Serial.print("deserializeJson() failed: ");
    Serial.println(error.c_str());
    return;
  }
  updateHomeUnitRefrestTime(jsonBuffer);
  updateBrightness(jsonBuffer);
}

void updateBrightness(DynamicJsonDocument data) {
  if (data.containsKey("brightness")) {
    lcd.setBrightness(data["brightness"]);
  } else {
    Serial.println("No brightness field in JSON");
  }
}

void updateHomeUnitRefrestTime(DynamicJsonDocument data) {
  if (data.containsKey("refreshTimeSec")) {
    int refreshTime = data["refreshTimeSec"];
    if (refreshTime != 0) {
      homeDisplayRefreshTimeSec = refreshTime;
    }
  } else {
    Serial.println("No refreshTimeSec field in JSON");
  }
}


void updateStationSettings(String json) {
  const size_t bufferSize = JSON_OBJECT_SIZE(5);
  DynamicJsonDocument jsonBuffer(bufferSize);
  DeserializationError error = deserializeJson(jsonBuffer, json);

  if (error) {
    Serial.print("deserializeJson() failed: ");
    Serial.println(error.c_str());
    return;
  }

  updateWeatherStationRefreshTime(jsonBuffer);
  updateTempOffset(jsonBuffer);
  updateHumidityOffset(jsonBuffer);
  updatePressureOffset(jsonBuffer);
}

void updatePressureOffset(DynamicJsonDocument data) {
    if (data.containsKey("pressureOffset")) {
    pressureOffset = data["pressureOffset"];
  } else {
    Serial.println("No pressureOffset field in JSON");
  }
}

void updateHumidityOffset(DynamicJsonDocument data) {
    if (data.containsKey("humidityOffset")) {
    humidityOffset = data["humidityOffset"];
  } else {
    Serial.println("No humidityOffset field in JSON");
  }
}

void updateTempOffset(DynamicJsonDocument data) {
    if (data.containsKey("tempOffset")) {
    tempOffset = data["tempOffset"];
  } else {
    Serial.println("No tempOffset field in JSON");
  }
}

void updateWeatherStationRefreshTime(DynamicJsonDocument data) {
  if (data.containsKey("refreshTimeSec")) {
    int refreshTime = data["refreshTimeSec"];
    if (refreshTime != 0) {
      weatherStationRefreshTimeSec = refreshTime;
    }
  } else {
    Serial.println("No refreshTimeSec field in JSON");
  }
}


void fetchTimestamp() {
  http.begin("http://" + host + ":8080/api/weather-reading/current-time");
  int responseCode = http.GET();
  if (responseCode == 200) {
    String payload = http.getString();
    timestamp = toLongLong(payload) - millis();
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

void refreshDisplayData() {
  lcd.print(roundTemp(readings[readingsCount - 1].temperature), 3, 0);
  lcd.print(String(readings[readingsCount - 1].humidity), 9, 0);
}

String roundTemp(String temperature) {
  char tempValue[20];
  temperature.toCharArray(tempValue, 20);
  String temp = (String) roundToNearestHalf(atof(tempValue));
  String result;
  for (int i = 0; i < temp.length(); i++) {
    if (temp[i] == '.') {
      if (temp[i + 1] == '0') {
        result = result + "  ";
        return result;
      } else {
        result = result + temp[i];
        result = result + temp[i + 1];
        return result;
      }
    }
    result = result + temp[i];
  }
  return result;
}

double roundToNearestHalf(double value) {
    double integerPart;
    double fractionalPart = modf(value, &integerPart);
    double roundedFractionalPart;

    if (fractionalPart < 0.25) {
        roundedFractionalPart = 0.0;
    } else if (fractionalPart < 0.75) {
        roundedFractionalPart = 0.5;
    } else {
        roundedFractionalPart = 1.0;
    }

    return integerPart + roundedFractionalPart;
}
