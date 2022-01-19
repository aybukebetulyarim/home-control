package com.iot.smarthome.data;


import com.google.gson.Gson;
import com.iot.smarthome.data.model.SensorResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiHelper {

    private final DataListener<SensorResponse> dataListener;
    private final OkHttpClient client = new OkHttpClient();
    private Request request;

    public ApiHelper(DataListener<SensorResponse> dataListener) {
        this.dataListener = dataListener;
    }

    public void init() {
        request = new Request.Builder()
                .url("https://smart-homecontrol.herokuapp.com/sensor")
                .build();
    }

    public void run() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {

                    if (responseBody != null) {
                        SensorResponse sensorResponse = new Gson().fromJson(responseBody.string(), SensorResponse.class);
                        dataListener.onChangeListener(sensorResponse);
                    }
                }
            }
        });
    }
}
