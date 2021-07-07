#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ArduinoJson.h>

#define FASTLED_ALLOW_INTERRUPTS 0

#include <FastLED.h>

#define PIN      2
#define N_LEDS 150
#define LED_TYPE NEOPIXEL
#define BRIGHTNESS 128

CRGB leds[N_LEDS];
CRGB primary;
CRGB secondary;
CRGB tertiary;

int i = 0;
int alternateFlag = 1;
int chaseFlag = 1;

unsigned long prevMillis = 0;
int mode = 1;

// Mode 0
void clearAnimation() {
  FastLED.clear();
  FastLED.show();
}

// Mode 1
static void chase() {
  if (mode != 1) { return; }
  if (i > N_LEDS+4) { i = 0; chaseFlag++; }
  if (chaseFlag > 3) { chaseFlag = 1; }
  
  unsigned long currMillis = millis();
  if (currMillis - prevMillis >= 20) {
    if (i < N_LEDS) {
      switch(chaseFlag) {
        case 1:
          leds[i] = primary;
          break;
        case 2:
          leds[i] = secondary;
          break;
        case 3:
          leds[i] = tertiary;
          break;
      }
    }
    if (i > 3) {
      leds[i-4] = 0;
    }
    prevMillis = currMillis;
    i++;
  }
}

// Mode 2
void alternateAnimation() {
  if (mode != 2) { return; }
  unsigned long currMillis = millis();
  
  switch(alternateFlag) {
    case 1:
      if (currMillis - prevMillis >= 20) {
        leds[i] = primary;
        prevMillis = currMillis;
        i += 3;
      }
      if (i > N_LEDS) {
        i = 1;
        alternateFlag = 2;
      }
      break;
      
    case 2:
      if (currMillis - prevMillis >= 20) {
        leds[i] = secondary;
        prevMillis = currMillis;
        i += 3;
      }
      if (i > N_LEDS) {
        i = 2;
        alternateFlag = 3;
      }
      break;

    case 3:
      if (currMillis - prevMillis >= 20) {
        leds[i] = tertiary;
        prevMillis = currMillis;
        i += 3;
      }
      if (i > N_LEDS) {
        i = 0;
        alternateFlag = 4;
      }
      break;
    case 4:
      if (currMillis - prevMillis >= 20) {
        leds[i] = 0;
        prevMillis = currMillis;
        i++;
      }
      if (i > N_LEDS) {
        i = 0;
        alternateFlag = 1;
      }
      break;
  }
}

// Mode 3
void randomFadeAnimation() {
  if (mode != 3) { return; }
  srand(time(0));
  int max = rand()%20;
  unsigned long currMillis = millis();

  // Set LED indices
  if (currMillis - prevMillis >= 20) {
    prevMillis = currMillis;
    for (int i = 0; i < max; i++) {
      leds[rand() % 150].r = rand() % 100 + 20;
    }
  }

  
  // Begin steps to deafen
  fadeToBlackBy(leds, N_LEDS, 20);
}

// Mode 4
void rainbowAnimation() {
  if (mode != 4 && mode != 5) { return; }

  unsigned long currMillis = millis();

  // Set LED indices
  if (currMillis - prevMillis >= 40) {
    prevMillis = currMillis;
    fill_rainbow(leds, N_LEDS, 10, 3);
  }
}

// Mode 5
void rainbowWithGlitterAnimation() {
  rainbowAnimation();
  addGlitter(40);
}

void addGlitter( fract8 chanceOfGlitter) 
{
  if( random8() < chanceOfGlitter) {
    leds[ random16(N_LEDS) ] += CRGB::White;
  }
}

// Mode 6
void bpm()
{
  if (mode != 6) { return; }
  // colored stripes pulsing at a defined Beats-Per-Minute (BPM)
  uint8_t BeatsPerMinute = 62;
  CRGBPalette16 palette = PartyColors_p;
  uint8_t beat = beatsin8( BeatsPerMinute, 64, 255);

  unsigned long currMillis = millis();
  if (currMillis - prevMillis >= 30) {
    if (i >= N_LEDS) { i = 0; }
    leds[i] = ColorFromPalette(palette, (i*2), beat+(i*10));
    i++;
  }
}



ESP8266WebServer server(80);

const char* wifi_user = "";
const char* wifi_pass = "";


void setup() {
  // put your setup code here, to run once:
  FastLED.addLeds<LED_TYPE, PIN>(leds, N_LEDS);
  FastLED.setBrightness(BRIGHTNESS);
  Serial.begin(115200);
  WiFi.begin(wifi_user, wifi_pass);
  FastLED.setMaxRefreshRate(200);

  primary = CRGB(255,0,0);
  secondary = CRGB(0, 255, 0);
  tertiary = CRGB(0, 0, 255);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("\nWaiting to connect... \n");
  }

  Serial.println("Connected!\n");
  Serial.println(WiFi.localIP());
  Serial.println("\n");
  server.on("/", HTTP_GET, handle_index);
  server.on("/animate", HTTP_POST, handle_change);

  server.begin();

}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
    switch (mode) {
      case 0:
        clearAnimation();
        break;
      case 1:
        chase();
        break;
      case 2:
        alternateAnimation();
        break;
      case 3:
        randomFadeAnimation();
        break;
      case 4:
        rainbowAnimation();
        break;
      case 5:
        rainbowWithGlitterAnimation();
        break;
      case 6:
        bpm();
        break;
      default:
        mode = 1;
    }
    FastLED.show();
  
  
}

void clearFlags() {
  FastLED.clear();
  i = 0;
  alternateFlag = 1;
  chaseFlag = 1;
}

void handle_index() {
  Serial.println("Found client: \n");
  server.send(200, "text/plain", "200: Ok");
}

void handle_change() {
  String message = server.arg("plain");
  StaticJsonBuffer<400> jBuffer;
  JsonObject& jObject = jBuffer.parseObject(message);
  mode = jObject["mode"];
  primary = CRGB(atoi(jObject["primary"]["red"]),
                 atoi(jObject["primary"]["green"]),
                 atoi(jObject["primary"]["blue"]));
                 
  secondary = CRGB(atoi(jObject["secondary"]["red"]),
                 atoi(jObject["secondary"]["green"]),
                 atoi(jObject["secondary"]["blue"]));

  tertiary = CRGB(atoi(jObject["tertiary"]["red"]),
                 atoi(jObject["tertiary"]["green"]),
                 atoi(jObject["tertiary"]["blue"]));

  Serial.print("Found client. ---------------------- \nMode found: ");
  Serial.print(mode);
  Serial.println();

  Serial.print("Color data found. \nPrimary: ");
  Serial.println(primary.r);
  Serial.print("Secondary: ");
  Serial.println(secondary.r);
  Serial.print("Tertiary: ");
  Serial.println(tertiary.r);
  Serial.print("\n\n");
  
  clearFlags();
  
  server.send(200, "text/plain", message);
}
