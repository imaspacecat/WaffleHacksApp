package dubiner.org.wafflehacksapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.staticfiles.Location;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textAccelerationX, textAccelerationY, textAccelerationZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final int PORT = 3030;

    private Map<String, Float> data;


    // brian variables
    int recordingMovement = 0;
    boolean debugMode = false;
    int strokeSize = 6;
    private Button button;

    int mDefaultColor;
    Button mButton;
    int Alpha = 255;
    int Red = 255;
    int Green = 0;
    int Blue = 0;

    Button debugButton;

    TextView display;
    String[] currentDisplay = new String[6];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAccelerationX = findViewById(R.id.textAccelerationX);
        textAccelerationY = findViewById(R.id.textAccelerationY);
        textAccelerationZ = findViewById(R.id.textAccelerationZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        Server server = new Server(PORT);
//        String html = "";
//        try {
//            reader = new InputStreamReader(Resources.getSystem().openRawResource(R.raw.index),
//                    StandardCharsets.UTF_8);
//
//            int data = reader.read();
//            while (data != -1) {
//                data = reader.read();
//                html += (char) data;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println("html is: \n" + html);


        Gson gson = new Gson();
        data = new LinkedHashMap<>();

        data.put("r", 0f);
        data.put("g", 0f);
        data.put("b", 0f);
        data.put("opacity", 0f);
        data.put("strokeWidth", 0f);

        Javalin app = Javalin.create().start(PORT);

        app.get("/", ctx -> ctx.html("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js\"></script>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <p>Hello world</p>\n" +
                "        <div id=\"x\"></div>\n" +
                "        <div id=\"y\"></div>\n" +
                "        <div id=\"z\"></div>\n" +
                "        \n" +
                "\n" +
                "        <script>\n" +
                "            var accelerationX = 0;\n" +
                "            var accelerationY = 0;\n" +
                "            var accelerationZ = 0;\n" +
                "            \n" +
                "            // draw initial graph\n" +
                "            // use variables to keep updating graph\n" +
                "\n" +
                "            setInterval(() => {\n" +
                "                $(\"#x\").load(\"http://localhost:3030/data/accelerationX\");\n" +
                "                $(\"#y\").load(\"http://localhost:3030/data/accelerationY\");\n" +
                "                $(\"#z\").load(\"http://localhost:3030/data/accelerationZ\");\n" +
                "\n" +
                "                $.get(\"http://localhost:3030/data/accelerationX\", function(data){\n" +
                "                    accelerationX = data;\n" +
                "                });\n" +
                "                \n" +
                "                $.get(\"http://localhost:3030/data/accelerationY\", function(data){\n" +
                "                    accelerationY = data;\n" +
                "                });\n" +
                "\n" +
                "                $.get(\"http://localhost:3030/data/accelerationZ\", function(data){\n" +
                "                    accelerationZ = data;\n" +
                "                });\n" +
                "\n" +
                "                // accelerationX = $.get(\"http://localhost:3030/data/accelerationX\");\n" +
                "                // accelerationY = $.get(\"http://localhost:3030/data/accelerationY\");\n" +
                "                // accelerationZ = $.get(\"http://localhost:3030/data/accelerationZ\");\n" +
                "                console.log(\"accelerationX: \" + accelerationX);\n" +
                "                console.log(\"accelerationY: \" + accelerationY);\n" +
                "                console.log(\"accelerationZ: \" + accelerationZ);\n" +
                "\n" +
                "            }, 500);\n" +
                "            \n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>"));

        app.get("/data/{name}", ctx -> {
            if(data.containsKey(ctx.pathParam("name"))) {
                ctx.result(String.valueOf(data.get(ctx.pathParam("name"))));
            } else{
                ctx.result("Please enter a valid name");
            }
        });

        app.get("/data", ctx -> {
            ctx.contentType("application/json").result(gson.toJson(data));
        });

        // brian code
        display = (TextView) findViewById(R.id.display);
        display.setBackgroundColor(Color.parseColor("lightgrey"));
        currentDisplay[0] = "Status: Stopped\n";
        currentDisplay[1] = "Color: (" + Red + ", " + Green + ", " + Blue + ")\n";
        currentDisplay[2] = "Stroke Size: " + strokeSize;
        currentDisplay[3] = ""; currentDisplay[4] = ""; currentDisplay[5] = "";
        updateDisplay(display);


        mDefaultColor = 0xffff0000;
        mButton = (Button) findViewById(R.id.colorPicker);
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openColorPicker();
            }
        });

        button = (Button) findViewById(R.id.info);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                infoButtonPressed();
            }
        });

        debugButton = (Button) findViewById(R.id.debug);
        debugButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                debugMode = !debugMode;
                debugPressed();
            }
        });

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v){
                if(recordingMovement == 0){
                    startButton.setText("Stop");
                    currentDisplay[0] = "Status: Running\n";
                    recordingMovement = 1;
                }else{
                    startButton.setText("Start");
                    recordingMovement = 0;
                    currentDisplay[0] = "Status: Stopped\n";
                }
                updateDisplay(display);
            }
        });

        Slider slider = findViewById(R.id.slider);
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            strokeSize = (int) value;
            currentDisplay[2] = "Stroke Size: " + strokeSize + "\n";
            updateDisplay(display);
        });
    }

    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                Alpha = Color.alpha(mDefaultColor);
                Red = Color.red(mDefaultColor);
                Green = Color.green(mDefaultColor);
                Blue = Color.blue(mDefaultColor);
                currentDisplay[1] = "Color: (" + Red + ", " + Green + ", " + Blue + ")\n";
                updateDisplay(display);
            }
        });
        colorPicker.show();

    }

    public void infoButtonPressed(){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
    }

    public void debugPressed(){
        debugMode = !debugMode;
    }

    @SuppressLint("SetTextI18n")
    private void updateDisplay(TextView display){
        display.setText(currentDisplay[0] + currentDisplay[1] + currentDisplay[2] + currentDisplay[3]
                + currentDisplay[4] + currentDisplay[5]);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        data.put("accelerationX", event.values[0]);
        data.put("accelerationY", event.values[1]);
        data.put("accelerationZ", event.values[2]);
        textAccelerationX.setText("Acceleration X: " + event.values[0]);
        textAccelerationY.setText("Acceleration Y: " + event.values[1]);
        textAccelerationZ.setText("Acceleration Z: " + event.values[2]);


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

//    public String initIpAddress() {
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        System.out.println("Server running at: " + ip + ":" + PORT);
//        return "http://" + ip + ":" + PORT + "/";
//    }
}