package dubiner.org.wafflehacksapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import io.javalin.Javalin;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textAccelerationX, textAccelerationY, textAccelerationZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final int PORT = 80;

    private float accelerationX, accelerationY, accelerationZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAccelerationX = findViewById(R.id.textAccelerationX);
        textAccelerationY = findViewById(R.id.textAccelerationY);
        textAccelerationZ = findViewById(R.id.textAccelerationZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Javalin app = Javalin.create().start(PORT);
        app.get("/", ctx -> ctx.result("Hello world"));
        System.out.println("Server started...");


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        accelerationX = event.values[0];
        accelerationY = event.values[1];
        accelerationZ = event.values[2];
        textAccelerationX.setText("Acceleration X: " + accelerationX);
        textAccelerationY.setText("Acceleration Y: " + accelerationY);
        textAccelerationZ.setText("Acceleration Z: " + accelerationZ);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public String initIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        System.out.println("Server running at: " + ip + ":" + PORT);
        return "http://" + ip + ":" + PORT + "/";
    }
}