package masterIoT.mdp.karma.missions;
import masterIoT.mdp.karma.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Iterator;

//ACTIVIDAD DE LAS MISIONES

public class MissionsActivity extends AppCompatActivity {

    // App-specific dataset:
    private static final MissionsDataset dataset = MissionsDataset.getInstance();
    private Button bAddMission;
    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private final MyOnMissionActivatedListener myOnMissionActivatedListener =
            new MyOnMissionActivatedListener(this, dataset);

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        recyclerView = findViewById(R.id.recyclerView);
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

        // Botón de añadir misión
        bAddMission = findViewById(R.id.addMission);
        bAddMission.setOnClickListener(v -> {
            AddMission dialog = new AddMission(this, dataset, recyclerViewAdapter);
            dialog.show();
        });

        if (savedInstanceState != null) {
            // Restore state related to selections previously made
            tracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState); // Save state about selections.
    }

    // ------ Buttons' on-click listeners ------ //

    public void listLayout(View view) {
        // Button to see in a linear fashion has been clicked:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void gridLayout(View view) {
        // Button to see in a grid fashion has been clicked:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void seeCurrentSelection(View view) {
        // Button "see current selection" has been clicked:

//        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
//        // This iterator allows to navigate through the keys of the currently selected items.
//        // Complete info on getSelection():
//        // https://developer.android.com/reference/androidx/recyclerview/selection/SelectionTracker#getSelection()
//        // Complete info on class Selection (getSelection() returns an object of this class):
//        // https://developer.android.com/reference/androidx/recyclerview/selection/Selection
//
//        String text = "";
//        while (iteratorSelectedItemsKeys.hasNext()) {
//            text += iteratorSelectedItemsKeys.next().toString();
//            if (iteratorSelectedItemsKeys.hasNext()) {
//                text += ", ";
//            }
//        }
//        text = "Keys of currently selected items = \n" + text;

//        Intent i = new Intent(this, SecondActivity.class);
//        i.putExtra("text", text);
//        startActivity(i);
    }

    public void deleteCurrentSelection(View view){
//        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
//
//        while (iteratorSelectedItemsKeys.hasNext()) {
//            dataset.removeMissionAtPosition(dataset.getPositionOfKey(iteratorSelectedItemsKeys.next()));
//            dataset.removeMissionWithKey(iteratorSelectedItemsKeys.next());
//        }
//
//        recyclerView.getAdapter().notifyDataSetChanged();
    }

}