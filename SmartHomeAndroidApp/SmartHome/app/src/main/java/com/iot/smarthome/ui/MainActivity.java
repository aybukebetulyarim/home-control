package com.iot.smarthome.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.iot.smarthome.R;
import com.iot.smarthome.data.ApiHelper;
import com.iot.smarthome.data.DataListener;
import com.iot.smarthome.data.model.SensorResponse;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements DataListener<SensorResponse> {

    private ApiHelper apiHelper;
    private final Timer timer = new Timer();

    private TextView gasValueTV;
    private TextView motionValueTV;
    private TextView humidityValueTV;
    private TextView temperatureValueTV;

    private LinearLayoutCompat gasViewLL;
    private LinearLayoutCompat motionViewLL;
    private LinearLayoutCompat humidityViewLL;
    private LinearLayoutCompat temperatureViewLL;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View includeGas = findViewById(R.id.gasIV);
        View includeMotion = findViewById(R.id.motionIV);
        View includeHumidity = findViewById(R.id.humidityIV);
        View includeTemperature = findViewById(R.id.temperatureIV);

        gasValueTV = includeGas.findViewById(R.id.gasValueTV);
        motionValueTV = includeMotion.findViewById(R.id.motionValueTV);
        humidityValueTV = includeHumidity.findViewById(R.id.humidityValueTV);
        temperatureValueTV = includeTemperature.findViewById(R.id.temperatureValueTV);

        gasViewLL = includeGas.findViewById(R.id.mainLL);
        motionViewLL = includeMotion.findViewById(R.id.mainLL);
        humidityViewLL = includeHumidity.findViewById(R.id.mainLL);
        temperatureViewLL = includeTemperature.findViewById(R.id.mainLL);

        apiHelper = new ApiHelper(this);
        apiHelper.init();
        timerInit();
    }

    private void timerInit() {
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    apiHelper.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onChangeListener(SensorResponse data) {
        String gas = data.getGas();
        String motion = data.getMotion();
        String humidity = data.getHumidity();
        String temperature = data.getTemperature();

        setGasCard(gas);
        setMotionCard(motion);
        setHumidityCard(humidity);
        setTemperatureCard(temperature);
    }

    private void setGasCard(String gas) {
        try {
            double _gas = Double.parseDouble(gas);
            gasValueTV.setText(String.valueOf(_gas));
            changeCardBackground(gasViewLL, _gas, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMotionCard(String motion) {
        try {
            int _motion = Integer.parseInt(motion);
            if (_motion > 0) {
                motionValueTV.setText("Yes");
            } else {
                motionValueTV.setText("No");
            }
            changeCardBackground(motionViewLL, _motion, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHumidityCard(String humidity) {
        try {
            double _humidity = Double.parseDouble(humidity);
            humidityValueTV.setText(String.valueOf(_humidity));
            changeCardBackground(humidityViewLL, _humidity, 35);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTemperatureCard(String temperature) {
        try {
            double _temperature = Double.parseDouble(temperature);
            temperatureValueTV.setText(String.valueOf(_temperature));
            changeCardBackground(temperatureViewLL, _temperature, 40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeCardBackground(LinearLayoutCompat linearLayoutCompat, double sourceValue, double referenceValue) {
        if (sourceValue > referenceValue) {
            linearLayoutCompat.setBackground(ContextCompat.getDrawable(this, R.drawable.card_danger_bg));
        } else {
            linearLayoutCompat.setBackground(ContextCompat.getDrawable(this, R.drawable.card_normal_bg));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
