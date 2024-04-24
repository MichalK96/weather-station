#include <LiquidCrystal_I2C.h>
#include "LcdPictograms.h"

class LcdManager {

public:
    LcdManager() : lcd(0x27, 20, 4), pictograms(lcd) {};
    LcdPictograms pictograms;

    void init() {
        lcd.init();
        pinMode(lcdPin, OUTPUT);
        analogWrite(lcdPin, 1023);
    };

    void setCursor(int x, int y) {
        lcd.setCursor(x, y);
    };

    void print(String text) {
        lcd.print(text);
    };

    void print(String text, int x, int y) {
        lcd.setCursor(x, y);
        lcd.print(text);
    };

    void clear() {
        lcd.clear();
    };

    void displayPictograms() {
        pictograms.tempIcon(0, 0);
    };

private:
    LiquidCrystal_I2C lcd;
    int lcdPin = D7;
    int lcdBrightness = 100;
};
