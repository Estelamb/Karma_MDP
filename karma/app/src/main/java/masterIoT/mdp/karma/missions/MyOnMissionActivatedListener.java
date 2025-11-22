/**
 * @file MyOnMissionActivatedListener.java
 * @brief Handles activation events for mission items in a RecyclerView.
 *
 * This class implements OnItemActivatedListener<Long> to respond to item
 * activations (e.g., taps or double clicks) in a RecyclerView using the
 * selection library. When a mission item is activated, it opens a dialog
 * showing detailed information about the mission.
 */

package masterIoT.mdp.karma.missions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

/**
 * @class MyOnMissionActivatedListener
 * @brief Listens for item activation events and shows mission details.
 *
 * This class is used with RecyclerView's SelectionTracker. When a mission
 * item is activated (e.g., tapped), it retrieves the corresponding mission
 * from the dataset and opens a MissionInfo dialog to show its details.
 */
public class MyOnMissionActivatedListener implements OnItemActivatedListener<Long> {

    /** Tag for logging purposes */
    private static final String TAG = "TAGListOfItems, MyOnItemActivatedListener";

    /** Application context */
    private final Context context;

    /** Reference to the missions dataset */
    private MissionsDataset dataset;

    /**
     * @brief Constructor for MyOnMissionActivatedListener.
     *
     * @param context Application context for creating dialogs or starting activities.
     * @param ds Reference to the dataset containing mission items.
     */
    public MyOnMissionActivatedListener(Context context, MissionsDataset ds) {
        this.context = context;
        this.dataset = ds;
    }

    /**
     * @brief Called when a mission item is activated (tapped or double-clicked).
     *
     * This method retrieves the mission at the activated item's position
     * and opens a MissionInfo dialog to display its details.
     *
     * @param itemdetails Details of the activated item, including its position and selection key.
     * @param e MotionEvent associated with the activation.
     * @return true if the activation was handled successfully.
     */
    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails itemdetails,
                                   @NonNull MotionEvent e) {
        Mission mission = dataset.getMissionAtPosition(itemdetails.getPosition());
        MissionInfo dialog = new MissionInfo(context, mission);
        dialog.show();
        return true;
    }
}
