package masterIoT.mdp.karma.missions;

import static android.content.ContentValues.TAG;

import masterIoT.mdp.karma.MQTT;
import masterIoT.mdp.karma.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;

/**
 * @class MissionsActivity
 * @brief Activity that displays all missions and handles user interactions.
 *
 * This activity shows a list or grid of missions, supports adding new missions,
 * viewing selection, and deleting selected missions. It uses MQTT to connect
 * and synchronize mission data.
 */
public class MissionsActivity extends AppCompatActivity {
    /** MQTT tag for logging. */
    private static final String TAG = "MQTT";

    /** Singleton dataset containing all missions. */
    private static final MissionsDataset dataset = MissionsDataset.getInstance();

    /** Button to add a new mission. */
    private Button bAddMission;

    /** RecyclerView displaying the missions. */
    private RecyclerView recyclerView;

    /** SelectionTracker for managing selected missions. */
    private SelectionTracker<Long> tracker;

    /** Listener triggered when a mission item is activated. */
    private final MyOnMissionActivatedListener myOnMissionActivatedListener =
            new MyOnMissionActivatedListener(this, dataset);

    /** Launcher for gallery image selection. */
    private ActivityResultLauncher<Intent> galleryLauncher;

    /** Launcher for camera capture. */
    private ActivityResultLauncher<Intent> cameraLauncher;

    /** Bitmap of selected image from gallery or camera. */
    private Bitmap selectedBitmap;

    /** MQTT client instance. */
    private MQTT mqttClient;

    /**
     * @brief Initializes the activity, sets up RecyclerView, selection, and add button.
     * @param savedInstanceState Previous saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);
        setupMQTT();

        recyclerView = findViewById(R.id.recyclerView);
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

        bAddMission = findViewById(R.id.addMission);
        bAddMission.setOnClickListener(v -> {
            AddMission dialog = new AddMission(this, dataset, recyclerViewAdapter);
            dialog.show();
        });

        if (savedInstanceState != null) {
            tracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    /**
     * @brief Saves the selection state before activity is destroyed.
     * @param outState Bundle to save state into.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState);
    }

    /**
     * @brief Switches RecyclerView to a linear list layout.
     * @param view Button that triggers this action.
     */
    public void listLayout(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * @brief Switches RecyclerView to a grid layout with 3 columns.
     * @param view Button that triggers this action.
     */
    public void gridLayout(View view) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    /**
     * @brief Placeholder for viewing currently selected missions.
     * @param view Button that triggers this action.
     */
    public void seeCurrentSelection(View view) {
        // Implementation commented out.
    }

    /**
     * @brief Placeholder for deleting currently selected missions.
     * @param view Button that triggers this action.
     */
    public void deleteCurrentSelection(View view){
        // Implementation commented out.
    }

    /**
     * @brief Disconnects MQTT client when activity is stopped.
     */
    @Override
    protected void onStop(){
        super.onStop();
        mqttClient.disconnect();
    }

    /**
     * @brief Disconnects MQTT client when activity is paused.
     */
    @Override
    protected void onPause(){
        super.onPause();
        mqttClient.disconnect();
    }

    /**
     * @brief Sets up MQTT client and connects to the server.
     */
    private void setupMQTT() {
        Log.i(TAG, "Connecting to MQTT");
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();
    }
}
