package masterIoT.mdp.karma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvKarma;
    private MQTT mqttClient;
    int karma_points = 0;
    private String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvKarma = findViewById(R.id.textView3);

        // Cargar karma points desde SharedPreferences (CORREGIDO)
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        karma_points = prefs.getInt("totalKarma", 0);
        tvKarma.setText(karma_points + " Karma Points");

        setupMQTT();
    }

    private void setupMQTT() {
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();

        tvKarma.postDelayed(new Runnable() {
            @Override
            public void run() {
                mqttClient.subscribe("mv/KarmaPoints", new MQTT.MessageCallback() {
                    @Override
                    public void onMessageReceived(String topic, String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    karma_points = Integer.parseInt(message.trim());

                                    // Guardar en shared preferences
                                    SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("totalKarma", karma_points);
                                    editor.apply();

                                } catch (NumberFormatException e) {
                                    tvKarma.setText("Error: " + message);
                                }
                            }
                        });
                    }
                });
            }
        }, 1000); // Esperar 1 segundo para conectar
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reconectar si es necesario
        if (mqttClient != null && !mqttClient.isConnected()) {
            mqttClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null) {
            mqttClient.unsubscribe("mv/KarmaPoints");
        }
    }
}