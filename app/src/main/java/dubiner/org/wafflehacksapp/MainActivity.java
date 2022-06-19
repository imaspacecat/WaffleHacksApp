package dubiner.org.wafflehacksapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.LinkedHashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textAccelerationX, textAccelerationY, textAccelerationZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final int PORT = 3030;

    public static Map<String, Float> data;


    // brian variables
    private int recordingMovement = 0;
    private boolean debugMode = false;
    private int strokeWidth = 6;
    private Button button;

    private int mDefaultColor;
    private Button mButton;
    private int alpha = 255;
    private int red = 255;
    private int green = 0;
    private int blue = 0;

    private Button debugButton;

    private TextView display;
    private String[] currentDisplay = new String[6];
    private Server server;

    int portNumber = 3030;
    EditText portInput;
    Button serverButton;
    boolean serverStarted = false;
    TextView serverDisplay;

    TextView colorDisplay;

    public static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        address = ip + ":" + PORT;
//        colorDisplay = (TextView) findViewById(R.id.colorDisplay);
//        colorDisplay.setBackgroundColor(0xffff0000);

        textAccelerationX = findViewById(R.id.textAccelerationX);
        textAccelerationY = findViewById(R.id.textAccelerationY);
        textAccelerationZ = findViewById(R.id.textAccelerationZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        data = new LinkedHashMap<>();
//        data.put("r", (float) red);
//        data.put("g", (float) green);
//        data.put("b", (float) blue);
//        data.put("strokeWidth", (float) strokeWidth);
//        data.put("opacity", (float) recordingMovement);

        // brian code
        display = (TextView) findViewById(R.id.display);
        display.setBackgroundColor(Color.parseColor("lightgrey"));
        currentDisplay[0] = "Status: Stopped\n";
//        currentDisplay[1] = "Color: (" + red + ", " + green + ", " + blue + ")\n";
//        currentDisplay[2] = "Stroke Size: " + strokeWidth;
        currentDisplay[3] = "";
        currentDisplay[4] = "";
        currentDisplay[5] = "";
        updateDisplay(display);


//        mDefaultColor = 0xffff0000;
//        mButton = (Button) findViewById(R.id.colorPicker);
//        mButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                openColorPicker();
//            }
//        });

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
            }
        });

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v){
                if(recordingMovement == 0){
                    try {
                        server = new Server(PORT);
                        initIpAddress();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    startButton.setText("Stop");
                    currentDisplay[0] = "Status: Running\n";
                    recordingMovement = 1;
                } else{
                    server.stop();
                    startButton.setText("Start");
                    recordingMovement = 0;
                    currentDisplay[0] = "Status: Stopped\n";
                }
                data.put("opacity", (float) recordingMovement);
                updateDisplay(display);
            }
        });

//        Slider slider = findViewById(R.id.slider);
//        slider.addOnChangeListener((slider1, value, fromUser) -> {
//            strokeWidth = (int) value;
//            data.put("strokeWidth", (float) strokeWidth);
//            currentDisplay[2] = "Stroke Size: " + strokeWidth + "\n";
//            updateDisplay(display);
//        });
        portInput = (EditText) findViewById(R.id.portInput);
        serverButton = (Button) findViewById(R.id.serverButton);
        serverButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                serverButtonClicked(v);
            }
        });

        serverDisplay = (TextView) findViewById(R.id.serverDisplay);
    }

//    public void openColorPicker(){
//        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
//            @Override
//            public void onCancel(AmbilWarnaDialog dialog) {
//
//            }
//
//            @Override
//            public void onOk(AmbilWarnaDialog dialog, int color) {
//                mDefaultColor = color;
//                alpha = Color.alpha(mDefaultColor);
//                red = Color.red(mDefaultColor);
//                green = Color.green(mDefaultColor);
//                blue = Color.blue(mDefaultColor);
//
//                data.put("r", (float) red);
//                data.put("g", (float) green);
//                data.put("b", (float) blue);
//
//                currentDisplay[1] = "Color: (" + red + ", " + green + ", " + blue + ")\n";
//                updateDisplay(display);
//
//                colorDisplay.setBackgroundColor(mDefaultColor);
//            }
//        });
//        colorPicker.show();
//    }

    public void infoButtonPressed(){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    private void updateDisplay(TextView display){
        display.setText(currentDisplay[0] + currentDisplay[3]
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

        if(debugMode) {
            currentDisplay[3] = "\nAccelX: " + event.values[0];
            currentDisplay[4] = "\nAccelY: " + event.values[1];
            currentDisplay[5] = "\nAccelZ: " + event.values[2];
            updateDisplay(display);
        } else{
            currentDisplay[3] = "";
            currentDisplay[4] = "";
            currentDisplay[5] = "";
            updateDisplay(display);
        }

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

    public void serverButtonClicked(View v){
        if(!serverStarted){

            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            String input = portInput.getText().toString();
            if(input.length() == 0){
                input = "3030";
                portNumber = 3030;
            }else{
                portNumber = Integer.parseInt(input);
            }

            input = "http://localhost:" + input + "/";

            serverDisplay.setText("Address: "+input);
            serverButton.setText("Disconnect");
            portInput.setFocusable(false);
        }else{
            serverDisplay.setText("Connect to a server!");
            serverButton.setText("Connect");
            portInput.setFocusableInTouchMode(true);
        }
        serverStarted = !serverStarted;
    }
    public String initIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        System.out.println("Server running at: " + ip + ":" + PORT);
        return ip + ":" + PORT + "/";
    }
}