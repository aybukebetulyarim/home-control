package com.iot.smarthome.data;

public interface DataListener<T> {

    void onChangeListener(T data);
}
