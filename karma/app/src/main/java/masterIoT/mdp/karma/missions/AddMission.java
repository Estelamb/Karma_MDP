/**
 * @file AddMission.java
 * @brief Handles the UI and logic for adding a new mission in the app.
 *
 * This class provides a dialog interface to input the mission's title, karma points,
 * description, and optionally an image. Once the user confirms, the mission is added
 * to the dataset and the RecyclerView is updated.
 */

package masterIoT.mdp.karma.missions;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import masterIoT.mdp.karma.R;

/**
 * @class AddMission
 * @brief Class for displaying a dialog to add a new mission.
 *
 * This class manages the dialog UI components and creates a new Mission object
 * based on user input. It updates the provided dataset and RecyclerView adapter
 * after adding a mission.
 */
public class AddMission {

    /** Application context used to create dialogs and inflate layouts. */
    private final Context context;

    /** Dataset that stores all missions. */
    private final MissionsDataset dataset;

    /** Adapter for updating the RecyclerView when a new mission is added. */
    private final RecyclerView.Adapter<?> adapter;

    /** Input field for the mission's title. */
    EditText inputTitle;

    /** Input field for the mission's karma points. */
    EditText inputKarma;

    /** Input field for the mission's description. */
    EditText inputDescription;

    /** ImageView for displaying a preview image (optional). */
    private ImageView previewImage;

    /**
     * @brief Constructs a new AddMission dialog manager.
     * @param context The application context.
     * @param dataset The dataset where missions are stored.
     * @param adapter The RecyclerView adapter to notify changes.
     */
    public AddMission(Context context, MissionsDataset dataset,
                      RecyclerView.Adapter<?> adapter) {
        this.context = context;
        this.dataset = dataset;
        this.adapter = adapter;
    }

    /**
     * @brief Displays the dialog for adding a new mission.
     *
     * Inflates the add mission layout, collects user input for title, description,
     * karma points, and optionally an image. On confirmation, a new Mission object
     * is created and added to the dataset, and the adapter is notified.
     */
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_mission, null);
        builder.setView(dialogView);

        inputTitle = dialogView.findViewById(R.id.inputTitle);
        inputKarma = dialogView.findViewById(R.id.inputKarma);
        inputDescription = dialogView.findViewById(R.id.inputDescription);
        previewImage = dialogView.findViewById(R.id.previewImage);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            int karmaPoints = 0;
            try {
                karmaPoints = Integer.parseInt(inputKarma.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            int imageRes = R.drawable.addmission;
            Mission newMission = new Mission(title, imageRes, karmaPoints, description, (long) dataset.getSize(), "", true);
            dataset.addMission(newMission);
            adapter.notifyItemInserted(dataset.getSize() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
