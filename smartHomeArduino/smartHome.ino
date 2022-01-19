#include "DHT.h"

#include <SoftwareSerial.h>
#include <DFRobot_sim808.h>

#define DHTPIN 2 
#define DHTTYPE DHT11 
DHT dht(DHTPIN, DHTTYPE);
#define PIN_TX 11
#define PIN_RX 10
String lat = "0";
String lon = "0";

String dtime = "";
String temperature = "0";
String humidity = "0";
String gas = "0";
String motion = "0";

bool motionState = false;
int motionStatus = 0;
int pirstate = 0;
int pirPin = 4;  
int ledPin = 7;

int smokeA0 = A5;
int sensorThres = 400;

String URL = "http://smart-homecontrol.herokuapp.com/";
SoftwareSerial client(PIN_TX,PIN_RX);
DFRobot_SIM808 sim808(&client);

void setup() {
    Serial.begin(9600);
    dht.begin();
    client.begin(9600);
    pinMode(ledPin, OUTPUT);
    pinMode(pirPin, INPUT); 
    pinMode(smokeA0, INPUT); 

    Serial.println("System started.");
   
    Serial.println("---------------------------------------");
    
    while(!sim808.init())
    {
        Serial.println("##Sim 808 başlatılamadı..");
        delay(100);
    }

    Serial.println("##Sim 808 başarılı.");
    Serial.println("##Sim APN ayarları yapılandırılıyor...");
    Serial.println("---------------------------------------");

    setConfig();
}

void loop() {
    getSensorGas();
    getSensorMotion();
    getSensorTemperature();
    getSensorHumidity();
    setHTTP();
    delay(1000);
}

int getSensorMotion() {
  pirstate = digitalRead(pirPin); 
  if(pirstate == HIGH)
    {
      digitalWrite(ledPin, HIGH); 
      //Serial.println("Motion Detected!");
      if(motionState == false){
      motionStatus = 1;
      motionState = true;
      }
    }
    else if (pirstate == LOW)
    {
      digitalWrite(ledPin, LOW);
      //Serial.println("Motion Ended");
      if(motionState == true){
      motionStatus = 0;
      motionState = false;
      }
    }
    return motionStatus;
}


int getSensorGas() {
  int analogSensor = analogRead(smokeA0);
  //Serial.print("Pin A0: ");
  //Serial.println(analogSensor);
  
  if (analogSensor > sensorThres)
  {
    //Serial.println("Gaz kaçağı var!!");
    //Serial.println("Gas: ");
    //Serial.println(analogSensor);
    return analogSensor;
    
  }
  else
  {
    //Serial.println("Gaz kaçağı yok!!");
    //Serial.println("Gas: ");
    //Serial.println(analogSensor);
    return analogSensor;
    
  }
  delay(100);
}

float getSensorTemperature() { 
  float temperature = dht.readTemperature();
  //Serial.println("Temperature: ");
  //Serial.println(temperature);
  return temperature;
}

float getSensorHumidity() {
  float humidity = dht.readHumidity();
  //Serial.println("Humidity: ");
  //Serial.println(humidity);
  return humidity;
}
  
void setConfig()
{
    clientPrint("AT+SAPBR=3,1,\"Contype\",\"GPRS\"");
    getRead();
    delay(1000);
    clientPrint("AT+SAPBR=3,1,\"APN\",\"internet\"");
    getRead();
    delay(1000);
    clientPrint("AT+SAPBR=3,1,\"USER\",\"\"");
    getRead();
    delay(1000);
    clientPrint("AT+SAPBR=3,1,\"PWD\",\"\"");
    getRead();
    delay(1000);
    clientPrint("AT+SAPBR=1,1");
    getRead();
    delay(1000);
}

void setHTTP()
{ 
      delay(100);
      temperature = String(getSensorTemperature());
      humidity = String(getSensorHumidity());
      gas = String(getSensorGas());
      motion = String(getSensorMotion());
      //Serial.println("Istek baslatildi");
      client.println("AT+HTTPINIT");
      getRead();
      delay(100);
      client.println("AT+HTTPPARA=\"REDIR\",1");
      getRead();
      delay(100);
      clientPrint("AT+HTTPPARA=\"CID\",1");
      getRead();
      delay(100);
      
      clientPrint("AT+HTTPPARA=\"URL\",\""+URL+"sensor-insert?temperature="+temperature+"&motion="+motion+"&gas="+gas+"&humidity="+humidity+"\"");
      //Serial.println("AT+HTTPPARA=\"URL\",\""+URL+"sensor-insert?temperature="+temperature+"&motion="+motion+"&gas="+gas+"&humidity="+humidity+"\"");
      getRead();
      delay(100);
      clientPrint("AT+HTTPACTION=0");
      getRead();
      delay(100);
      clientPrint("AT+HTTPREAD");
      getRead();
    //Serial.println("Istek tamamlandı......");
      delay(100);
      
}

void clientPrint(String log){
  client.println(log);
  
}


void getRead()
{
    while (client.available())
    {
       Serial.write(client.read());
    } 
}
