package com.iot.smarthome.data.model;

import com.google.gson.annotations.SerializedName;

public class SensorResponse {

    @SerializedName("gas")
    private String gas;

    @SerializedName("humidity")
    private String humidity;

    @SerializedName("motion")
    private String motion;

    @SerializedName("temperature")
    private String temperature;

    @SerializedName("timestamp")
    private long timestamp;

    public String getGas() {
        return gas;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getMotion() {
        return motion;
    }

    public String getTemperature() {
        return temperature;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}