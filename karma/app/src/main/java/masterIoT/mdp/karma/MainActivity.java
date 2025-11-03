package masterIoT.mdp.karma;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import masterIoT.mdp.karma.missions.MissionsActivity;


//IMPORTANTE CHICOS, EN EL XML DEL LAYOUT HAY UN TEXTVIEW CON VISIBILIDAD GONE PARA LA CUENTA DE LOS PASOS
//SOLO HACE FALTA CAMBIARLE LA VISIBILIDAD Y PONER LA CANTIDAD DE PASOS

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Button bMissions, bBoard;
    private ImageView bProfile;
    private SensorManager sensorManager;
    private Sensor step;
    private Switch stepSwitch;
    private boolean stepSensorAct;
    private int numbSteps;
    private Vibrator vibrator;
    private TextView mensajesMotivados, tvSteps;
    private Handler handler;
    private Runnable runnable;
    private List<String> mensajes;
    private Random random;
    private static final int REQUEST_ACTIVITY_RECOGNITION=1001;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_ACTIVITY_RECOGNITION);
        }

        bMissions=findViewById(R.id.btnMissions);
        bBoard=findViewById(R.id.btnLeaderBoard);
        bProfile=findViewById(R.id.imageView);

        stepSensorAct=false;
        stepSwitch=findViewById(R.id.swSteps);
        tvSteps=findViewById(R.id.txvwNumeroPasos);

        bMissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MissionsActivity.class);
                startActivity(intent);
            }
        });

        bBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                startActivity(intent);
            }
        });

        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        step=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);

        stepSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                sensorManager.registerListener(MainActivity.this, step, SensorManager.SENSOR_DELAY_NORMAL);
                tvSteps.setVisibility(View.VISIBLE);
                tvSteps.setText("Waiting for first step sensor value");
                stepSensorAct=true;
            }else{
                sensorManager.unregisterListener(MainActivity.this, step);
                stepSensorAct=false;
                tvSteps.setText("Proximity sensor is OFF");
                tvSteps.setVisibility(View.GONE);
            }
        });

        mensajesMotivados=findViewById(R.id.txvwMensajesPositivos);
        mensajes = Arrays.asList("Believe in yourself and all that you are.", "Every day is a new beginning.", "You are stronger than you think.", "Dream big and dare to fail.", "Keep going — you’re doing great!", "The best time for new beginnings is now.", "Be the energy you want to attract.", "Your only limit is your mind.", "Small steps every day lead to big results.", "Stay positive, work hard, make it happen.");
        random= new Random();
        handler= new Handler();
        runnable= new Runnable(){
            @Override
            public void run(){
                String mensaje=mensajes.get(random.nextInt((mensajes.size())));
                mensajesMotivados.setText(mensaje);
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detiene el handler cuando la actividad se destruye
        handler.removeCallbacks(runnable);
    }
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences prefs= getSharedPreferences("SensorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("totalSteps", numbSteps);
        editor.putBoolean("estadoStep", stepSensorAct);
        editor.commit();
        sensorManager.unregisterListener(MainActivity.this);
    }
    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences prefs=getSharedPreferences("SensorData", Context.MODE_PRIVATE);
        numbSteps= prefs.getInt("totalSteps", -1);
        boolean savedStepState= prefs.getBoolean("estadoStep", false);

        stepSensorAct = savedStepState;

        if(savedStepState){
            sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_NORMAL);
            if (numbSteps!=-1f){
                tvSteps.setText("Pasos: "+numbSteps);
            }else{
                tvSteps.setText("Waiting for first step detection value");
            }
        }else{
            tvSteps.setText("Step detector sensor is OFF");
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        System.out.println(sensorEvent.sensor.getType());
        if (sensorEvent.sensor.getType()==18){
            numbSteps= (int)sensorEvent.values[0] +numbSteps;
            if(numbSteps%250==0){vibrator.vibrate(1000);}
            tvSteps.setText("Pasos: "+numbSteps);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (stepSensorAct) {
                    step = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                    sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_NORMAL);
                }
                tvSteps.setText("Step detector ready");
            } else {
                stepSensorAct = false; // Asegurarse de no registrar el sensor
                tvSteps.setText("Denied permiss");
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos hacer nada aquí, pero el método debe estar presente
    }

}