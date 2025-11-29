package masterIoT.mdp.karma;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import masterIoT.mdp.karma.missions.MissionsActivity;

/**
 * @brief Main activity of the Karma app.
 * @details Handles UI initialization, step sensor management, motivational messages,
 *          user profile handling, SharedPreferences storage and MQTT communication.
 */
public class MainActivity extends AppCompatActivity /*implements SensorEventListener*/ {

    /** TAG for MQTT logs */
    private static final String TAG = "MQTT";

    /** Missions button */
    private Button bMissions, bBoard;

    /** Profile button */
    private ImageView bProfile;

    /** Sensor manager */
    private SensorManager sensorManager;

    /** Step detector sensor */
    private Sensor step;

    /** Switch to activate step counting */
    private Switch stepSwitch;

    /** Indicates if step sensor is active */
    private boolean stepSensorAct;

    /** Number of counted steps */
    private int numbSteps;

    /** Total karma points */
    private int karmaPoints;

    /** Vibrator for feedback */
    private Vibrator vibrator;

    /** Motivational messages text view */
    private TextView mensajesMotivados, tvSteps, tvKarma;

    /** Handler for timed messages */
    private Handler handler;

    /** Runnable for motivational message updates */
    private Runnable runnable;

    /** List of motivational phrases */
    private List<String> mensajes;

    /** Random generator for messages */
    private Random random;

    /** Request code for activity recognition */
    private static final int REQUEST_ACTIVITY_RECOGNITION = 1001;

    // SharedPreferences keys
    /** Preferences filename */
    private static final String PREFS_NAME = "KarmaAppPrefs";

    /** Username key */
    private static final String KEY_USERNAME = "username";

    /** First time key */
    private static final String KEY_FIRST_TIME = "first_time";

    /** MQTT client instance */
    private MQTT mqttClient;

    /**
     * @brief Called when the activity is created.
     * @param savedInstanceState Instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // First-time check
        checkFirstTime();



        // Request sensor permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_ACTIVITY_RECOGNITION);
        }

        bMissions = findViewById(R.id.btnMissions);
        bBoard = findViewById(R.id.btnLeaderBoard);
        bProfile = findViewById(R.id.imageView);
        tvKarma = findViewById(R.id.tvKarmaPoints);

        stepSensorAct = false;
        stepSwitch = findViewById(R.id.swSteps);
        tvSteps = findViewById(R.id.txvwNumeroPasos);

        // Load karma
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        karmaPoints = prefs.getInt("totalKarma", 0);
        tvKarma.setText(String.valueOf(karmaPoints));

        setupMQTT();

        bMissions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MissionsActivity.class);
            startActivity(intent);
        });

        bBoard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BoardActivity.class);
            startActivity(intent);
        });

        bProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Sensor configuration
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        step = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        stepsPermission();
        // Step switch listener
        stepSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
                        != PackageManager.PERMISSION_GRANTED) {
                    stepsPermission();
                }else {
                    Intent serviceIntent = new Intent(this, StepService.class);
                    ContextCompat.startForegroundService(this, serviceIntent);
                    tvSteps.setVisibility(View.VISIBLE);
                    SharedPreferences prefsSensor = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
                    numbSteps = prefsSensor.getInt("totalSteps", -1);
                    tvSteps.setText("Steps: " + prefsSensor.getInt("totalSteps", 0));
                }
            } else {
                Intent serviceIntent = new Intent(this, StepService.class);
                stopService(serviceIntent);
                tvSteps.setVisibility(View.GONE);
            }
        });

        // Motivational messages loop
        mensajesMotivados = findViewById(R.id.txvwMensajesPositivos);
        mensajes = Arrays.asList("Believe in yourself and all that you are.",
                "Every day is a new beginning.", "You are stronger than you think.",
                "Dream big and dare to fail.", "Keep going — you're doing great!",
                "The best time for new beginnings is now.", "Be the energy you want to attract.",
                "Your only limit is your mind.", "Small steps every day lead to big results.",
                "Stay positive, work hard, make it happen.");

        random = new Random();
        handler = new Handler();
        runnable = () -> {
            String mensaje = mensajes.get(random.nextInt((mensajes.size())));
            mensajesMotivados.setText(mensaje);
            handler.postDelayed(runnable, 10000);
        };
        handler.post(runnable);


        IntentFilter filter = new IntentFilter(getPackageName() + ".STEP_UPDATE");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(stepReceiver, filter, Context.RECEIVER_EXPORTED);
//        } else {
//            registerReceiver(stepReceiver, filter);
//        }


    }

    /**
     * @brief Checks if the user launches the app for the first time.
     */
    private void checkFirstTime() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean(KEY_FIRST_TIME, true);

        if (isFirstTime) {
            showUsernameDialog();
        } else {
            String username = prefs.getString(KEY_USERNAME, "");
            if (!username.isEmpty()) {
                Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @brief Shows dialog to request the username.
     */
    private void showUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome\n");
        builder.setMessage("Please, enter your username:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("User name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (!username.isEmpty()) {
                saveUsername(username);
                Toast.makeText(MainActivity.this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * @brief Saves the username in SharedPreferences.
     * @param username Username entered by the user.
     */
    private void saveUsername(String username) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_FIRST_TIME, false);
        editor.apply();
    }

    /**
     * @brief Returns the stored username.
     * @return Username string.
     */
    public String getUsername() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, "");
    }

    /** @brief Removes callbacks when activity is destroyed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    /** @brief Saves step state and disconnects MQTT when activity stops. */
    @Override
    protected void onStop() {
        super.onStop();
//        SharedPreferences prefs = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("totalSteps", numbSteps);
//        editor.putBoolean("estadoStep", stepSensorAct);
//        editor.commit();
        //sensorManager.unregisterListener(MainActivity.this);
        //mqttClient.disconnect();
    }

    /** @brief Loads saved sensor state when activity starts. */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /** @brief Saves karma and steps and disconnects MQTT on pause. */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("totalKarma", karmaPoints);
        editor.apply();
        unregisterReceiver(stepReceiver);
        SharedPreferences prefsSensor = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSensor = prefs.edit();
        editorSensor.putInt("totalSteps", numbSteps);
        editorSensor.putBoolean("estadoStep", stepSensorAct);
        editor.commit();
        //mqttClient.disconnect();
    }

    /** @brief Reloads karma and steps and reconnects MQTT on resume. */


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        karmaPoints = prefs.getInt("totalKarma", 0);
        tvKarma.setText(String.valueOf(karmaPoints));
        setupMQTT();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stepReceiver, new IntentFilter(getPackageName()+".STEP_UPDATE"), Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(stepReceiver, new IntentFilter(getPackageName()+".STEP_UPDATE"));
        }
        SharedPreferences prefsSensor = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
        numbSteps = prefsSensor.getInt("totalSteps", -1);
        boolean savedStepState = prefs.getBoolean("estadoStep", false);
        tvSteps.setText("Pasos: " + numbSteps);
        stepSensorAct = savedStepState;

//        if (savedStepState) {
//            //sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_NORMAL);
//            if (numbSteps != -1f) {
//                tvSteps.setText("Pasos: " + numbSteps);
//            } else {
//                tvSteps.setText("Waiting for first step detection value");
//            }
//        } else {
//            //tvSteps.setText("Step detector sensor is OFF");
//        }
    }



    /**
     * @brief Handles permission request results.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (stepSensorAct) {
                    step = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                    //sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_NORMAL);
                }
                tvSteps.setText("Step detector ready");
            } else {
                stepSensorAct = false;
                tvSteps.setText("Denied permiss");
            }
        }
    }



    /**
     * @brief Initializes and connects MQTT client.
     */
    private void setupMQTT() {
        Log.i(TAG, "ENTRE EN MQTT");
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();
//        Toast.makeText(this,
//                getSharedPreferences("UsersKarma", Context.MODE_PRIVATE).getAll().toString(),
//                Toast.LENGTH_LONG).show();
    }

    /**
     * @brief Requests activity recognition permission for the activity in the background.
     */
    private void stepsPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACTIVITY_RECOGNITION
                }, 1);
            }
        }
    }

    /**
     * @brief Handles step sensor updates.
     */
    private final BroadcastReceiver stepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences prefs = getSharedPreferences("SensorData", Context.MODE_PRIVATE);
            numbSteps = prefs.getInt("totalSteps", 0);
            Log.d("StepService", "Receiving steps");
            tvSteps.setText("Steps: " + numbSteps);
        }
    };
}
