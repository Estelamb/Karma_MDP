package masterIoT.mdp.karma;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;

public class BoardActivity extends AppCompatActivity {
    private ImageView bProfile;
    private TextView tvKarma, tvTest;
    private MQTT mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bProfile=findViewById(R.id.imageView);
        tvKarma=findViewById(R.id.tvKarmaPoints);
        tvTest=findViewById(R.id.textViewTest);
        SharedPreferences prefs=getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        int karmaPoints = prefs.getInt("totalKarma", 0);
        tvKarma.setText(String.valueOf(karmaPoints));
        setupMQTT();
        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        tvTest.setText(getSharedPreferences("UsersKarma",Context.MODE_PRIVATE).getAll().toString());
    }

    private void setupMQTT() {
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mqttClient.subscribe("app/users/+/karmaTotal", new MQTT.MessageCallback() {
//                    @Override
//                    public void onMessageReceived(String topic, String message) {
//                        Log.i(TAG, "🔴🔴🔴 MENSAJE MQTT RECIBIDO - Topic: " + topic + ", Mensaje: " + message);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Log.i(TAG, "🟢 Procesando mensaje en UI: " + message);
//                                    Log.i(TAG, "RECIBIDO: " + topic + "||" + message);
//
////                                    int karma = Integer.parseInt(message.trim());
////
////                                    // Guardar en SharedPreferences
////                                    SharedPreferences prefs = getSharedPreferences("Subs", Context.MODE_PRIVATE);
////                                    SharedPreferences.Editor editor = prefs.edit();
////                                    editor.putInt("totalKarma", karma);
////                                    editor.apply();
////                                    Log.i(TAG, "🟢 Karma guardado: " + karma);
//
//                                    // Actualizar UI
//                                    //tvTest.setText(String.valueOf(karma));
//
//                                } catch (NumberFormatException e) {
//                                    Log.e(TAG, "Error parseando - " + message);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }, 1000);
    }

}