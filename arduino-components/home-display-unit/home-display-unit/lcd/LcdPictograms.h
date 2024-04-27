
#if defined(ARDUINO) && ARDUINO >= 100
#define printByte(args)  write(args);
#else
#define printByte(args)  print(args,BYTE);
#endif

#include <LiquidCrystal_I2C.h>

class LcdPictograms {

public:
  LcdPictograms(LiquidCrystal_I2C& lcd) : lcd(lcd) {};

  void tempIcon(int x, int y) {
    lcd.createChar(0, (uint8_t*) temperature);
    lcd.setCursor(x, y);
    lcd.printByte(0);
    };

  void humidityIcon(int x, int y) {
    lcd.setCursor(x, y);
    lcd.print("%");
  }

  void dotIcon(int x, int y) {
    lcd.createChar(1, (uint8_t*) dot);
    lcd.setCursor(x, y);
    lcd.printByte(1);
  }

  void pressureIcon(int x, int y) {
    lcd.createChar(2, (uint8_t*) pressure);
    lcd.setCursor(x, y);
    lcd.printByte(2); 
  }

  void sunIcon(int x, int y) {
    lcd.createChar(3, (uint8_t*) sun);
    lcd.setCursor(x, y);
    lcd.printByte(3); 
  }

  void homeIcon(int x, int y) {
    lcd.createChar(4, (uint8_t*) home);
    lcd.setCursor(x, y);
    lcd.printByte(4); 
  }

  void treeIcon(int x, int y) {
    lcd.createChar(5, (uint8_t*) tree);
    lcd.setCursor(x, y);
    lcd.printByte(5); 
  }

  void lightIcon(int x, int y) {
    lcd.createChar(6, (uint8_t*) light);
    lcd.setCursor(x, y);
    lcd.printByte(6); 
  }

private:
  LiquidCrystal_I2C& lcd;

  const uint8_t temperature[8] = {
    B00100,
    B01010,
    B01010,
    B01010,
    B01010,
    B10001,
    B10001,
    B01110
  };

  const uint8_t dot[8] = {
    B00000,
    B00000,
    B00000,
    B00000,
    B00000,
    B00000,
    B01100,
    B01100
  };

  const uint8_t pressure[8] = {
    B10111,
    B11111,
    B10100,
    B00000,
    B00100,
    B01010,
    B11111,
    B10001
  };

    const uint8_t sun[8] = {
    B00000,
    B10001,
    B01110,
    B11111,
    B11111,
    B01110,
    B10001,
    B00000
  };

    const uint8_t home[8] = {
    B00000,
    B00000,
    B00100,
    B01110,
    B11111,
    B01010,
    B01110,
    B11111
  };

    const uint8_t tree[8] = {
    B00100,
    B00100,
    B01110,
    B01110,
    B11111,
    B00100,
    B00100,
    B11111
  };

    const uint8_t light[8] = {
    B00000,
    B00100,
    B00100,
    B01110,
    B10001,
    B00000,
    B00000,
    B00000
  };

};
