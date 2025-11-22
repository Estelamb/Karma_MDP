package masterIoT.mdp.karma.missions;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @class MyMissionDetailsLookup
 * @brief Maps MotionEvent to RecyclerView item details for selection tracking.
 *
 * This class extends ItemDetailsLookup<Long> and provides the SelectionTracker
 * with the position and selection key of the mission item under a touch event.
 */
final public class MyMissionDetailsLookup extends ItemDetailsLookup<Long> {

    /** Tag for logging purposes. */
    private static final String TAG = "TAGListOfMissions, MyMissionDetailsLookup";

    /** Reference to the RecyclerView. */
    private final RecyclerView mRecyclerView;

    /**
     * @brief Constructor for MyMissionDetailsLookup.
     * @param recyclerView The RecyclerView that contains mission items.
     */
    @SuppressLint("LongLogTag")
    public MyMissionDetailsLookup(RecyclerView recyclerView) {
        Log.d(TAG, "MyMissionDetailsLookup() called");
        mRecyclerView = recyclerView;
    }

    /**
     * @brief Returns item details corresponding to a MotionEvent.
     *
     * Maps the touch coordinates to a child view in the RecyclerView and retrieves
     * the ViewHolder, position, and key of the mission item.
     *
     * @param e MotionEvent from the user interaction.
     * @return ItemDetails<Long> containing position and selection key, or null if no item found.
     */
    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        Log.d(TAG, "getMissionDetails() called for a given MotionEvent");
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof MyViewHolder) {
                int positionOfTheHolder = holder.getAbsoluteAdapterPosition();
                Long keyOfTheHolder = ((MyAdapter) holder.getBindingAdapter()).getKeyAtPosition(positionOfTheHolder);

                ItemDetails<Long> missionDetails = new ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return positionOfTheHolder;
                    }
                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return keyOfTheHolder;
                    }
                };

                return missionDetails;
            }
        }
        return null;
    }
}
