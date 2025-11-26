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

import masterIoT.mdp.karma.missions.Mission;
import masterIoT.mdp.karma.missions.MissionsDataset;
import masterIoT.mdp.karma.missions.MyAdapter;
import masterIoT.mdp.karma.missions.MyMissionDetailsLookup;
import masterIoT.mdp.karma.missions.MyMissionKeyProvider;
import masterIoT.mdp.karma.missions.MyOnMissionActivatedListener;
import masterIoT.mdp.karma.missions.UserMissionsDataset;

/**
 * @class ProfileActivity
 * @brief Activity showing the user's profile information and mission list.
 *
 * This activity manages the user's karma display, mission list interaction,
 * deletion of missions, and MQTT connectivity used to synchronize mission data.
 */
public class ProfileActivity extends AppCompatActivity {

    /** TextView displaying the user's karma points. */
    private TextView tvKarma;

    /** TextView displaying the user's name. */
    private TextView tvName;

    /** Button used to delete the currently selected missions. */
    private Button btnDelete;

    /** MQTT client instance for handling MQTT updates. */
    private MQTT mqttClient;

    /** Total karma points for the current user. */
    int karma_points = 0;

    /** Log tag for debugging output. */
    private String TAG = "ProfileActivity";

    /** RecyclerView showing the list of missions. */
    private RecyclerView recyclerView;

    /** SelectionTracker for multiselect operations on mission items. */
    private SelectionTracker<Long> tracker;

    /** Dataset containing missions assigned specifically to the user. */
    private UserMissionsDataset dataset = new UserMissionsDataset();

    /** Listener used when mission items are activated. */
    private final MyOnMissionActivatedListener myOnMissionActivatedListener =
            new MyOnMissionActivatedListener(this, dataset);

    /**
     * @brief Called when the activity is created.
     *
     * Initializes UI elements, loads user data, configures RecyclerView for missions,
     * and establishes MQTT communication.
     *
     * @param savedInstanceState Saved activity state, if any.
     */
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
        tvName = findViewById(R.id.NameTextView);

        String username = getUsername();
        tvName.setText(username + " Profile: ");

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
                new MyMissionDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(myOnMissionActivatedListener)
                .build();

        recyclerViewAdapter.setSelectionTracker(tracker);

        // Load stored karma points
        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        karma_points = prefs.getInt("totalKarma", 0);
        tvKarma.setText(karma_points + " Karma Points");

        setupMQTT();
    }

    /**
     * @brief Initializes the MQTT client and connects to the broker.
     *
     * Subscriptions can be added here if needed for dynamic profile updates.
     */
    private void setupMQTT() {
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();
    }

    /**
     * @brief Retrieves the saved username from SharedPreferences.
     * @return Stored username, or "Usuario" if no value is found.
     */
    private String getUsername() {
        SharedPreferences prefs = getSharedPreferences("KarmaAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "Usuario");
    }

    /**
     * @brief Called when the activity becomes visible.
     *
     * Ensures MQTT is connected after returning from background.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mqttClient != null && !mqttClient.isConnected()) {
            mqttClient.connect();
        }
    }

    /**
     * @brief Called before the activity is destroyed.
     *
     * Unsubscribes profile-related topics from MQTT.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null) {
            mqttClient.unsubscribeAll("mv/KarmaPoints");
        }
    }

    /**
     * @brief Deletes all selected missions from the profile mission list.
     *
     * Removes missions from both the user dataset and the shared MissionsDataset.
     *
     * @param view Reference to the delete button.
     */
    public void deleteCurrentSelection(View view){
        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();

        MissionsDataset missionsDataset = MissionsDataset.getInstance();
        while (iteratorSelectedItemsKeys.hasNext()) {
            Long key = iteratorSelectedItemsKeys.next();
            dataset.removeMissionWithKey(key);
            missionsDataset.removeMissionWithKey(key);
            deleteMission(key);
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * @brief Deletes a mission created by the user via MQTT.
     * @param key Key of the mission to be deleted.
     */
    private void deleteMission(long key){
        String username=getUsername();
        //mqttClient.publish("app/addPuntos",String.valueOf(points), false);
        String message= username+":"+ key;

        mqttClient.publish("app/users/"+username+"/missionDelete", message,true);
    }
}
