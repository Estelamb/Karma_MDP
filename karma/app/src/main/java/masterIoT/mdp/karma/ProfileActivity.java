package masterIoT.mdp.karma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;

import masterIoT.mdp.karma.missions.MissionsDataset;
import masterIoT.mdp.karma.missions.MyAdapter;
import masterIoT.mdp.karma.missions.MyMissionDetailsLookup;
import masterIoT.mdp.karma.missions.MyMissionKeyProvider;
import masterIoT.mdp.karma.missions.MyOnMissionActivatedListener;
import masterIoT.mdp.karma.missions.UserMissionsDataset;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvKarma, tvName;
    private Button btnDelete;
    private MQTT mqttClient;
    int karma_points = 0;
    private String TAG = "ProfileActivity";
    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private UserMissionsDataset dataset = new UserMissionsDataset();
    private final MyOnMissionActivatedListener myOnMissionActivatedListener =
            new MyOnMissionActivatedListener(this, dataset);

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
        tvName=findViewById(R.id.NameTextView);
        String username= getUsername();
        tvName.setText(username+" Profile: ");

        btnDelete = findViewById(R.id.button2);
        btnDelete.setOnClickListener(this::deleteCurrentSelection);
        recyclerView = findViewById(R.id.recyclerProf);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyMissionKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyMissionDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(myOnMissionActivatedListener)
                .build();

        recyclerViewAdapter.setSelectionTracker(tracker);


        // Cargar karma points desde SharedPreferences (CORREGIDO)
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        karma_points = prefs.getInt("totalKarma", 0);
        tvKarma.setText(karma_points + " Karma Points");

        setupMQTT();
    }

    private void setupMQTT() {
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();

//        tvKarma.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mqttClient.subscribe("mv/KarmaPoints", new MQTT.MessageCallback() {
//                    @Override
//                    public void onMessageReceived(String topic, String message) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    karma_points = Integer.parseInt(message.trim());
//
//                                    // Guardar en shared preferences
//                                    SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = prefs.edit();
//                                    editor.putInt("totalKarma", karma_points);
//                                    editor.apply();
//
//                                } catch (NumberFormatException e) {
//                                    tvKarma.setText("Error: " + message);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }, 1000); // Esperar 1 segundo para conectar
    }

    private String getUsername() {
        SharedPreferences prefs = getSharedPreferences("KarmaAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "Usuario"); // "Usuario" es valor por defecto
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
            mqttClient.unsubscribeAll("mv/KarmaPoints");
        }
    }

    public void deleteCurrentSelection(View view){
        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();

        MissionsDataset missionsDataset = MissionsDataset.getInstance();
        while (iteratorSelectedItemsKeys.hasNext()) {
            Long key = iteratorSelectedItemsKeys.next();
            dataset.removeMissionWithKey(key);
            missionsDataset.removeMissionWithKey(key);
        }


        recyclerView.getAdapter().notifyDataSetChanged();
    }
}