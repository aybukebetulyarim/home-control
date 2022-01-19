## Home Control System
With the home control system, I designed a project in which I
measured the gas leakage in the house, whether there is anyone other than us if we live alone,
the temperature of the house, the humidity of the house and display these measurements in
the mobile application. I will use heat, humidity, temperature and motion sensors in my
project. Thanks to this project, when we are not at home, we will be able to follow the events
that happen or may happen in our house on our phone. Arduino will read data from home
every 10 seconds. By inserting the SIM card into the GSM module, we will be able to follow
the Arduino even when we go out of the house. The GSM module supports the HTTP
protocol. Thanks to the HTTP protocol, the read sensor data is sent to the back-end service
written with Python's Flask framework. The back-end service transmits and writes the data
transmitted over the GSM Module (HTTP request) to the database I created with MongoDB.
The application developed for the Android mobile device sends a request to the back-end
service to receive data every 10 seconds and displays the latest information in the application.
Reference ranges for data such as temperature, humidity, gas, movement will be determined
in the app beforehand. When it goes out of these reference ranges, the user will be informed
via the mobile application.
