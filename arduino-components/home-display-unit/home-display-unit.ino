#include <ESP8266WiFi.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27,20,4);
int count = 0;

void setup() {
    Serial.begin(115200);

    lcd.init();
    lcd.backlight();

    lcd.setCursor(3,0);
    lcd.print("Hello, world!");
    lcd.setCursor(2,1);
    lcd.print("Ywrobot Arduino!");
    lcd.setCursor(0,2);
    lcd.print("Arduino LCM IIC 2004");
    lcd.setCursor(2,3);
    lcd.print("Power By Ec-yuan!");

}

void loop() {
	Serial.print("Test petli: ");
    Serial.println(count);
    count++;
    delay(1000);
}
