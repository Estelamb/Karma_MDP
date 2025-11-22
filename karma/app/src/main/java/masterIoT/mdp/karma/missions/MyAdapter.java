package masterIoT.mdp.karma.missions;

import masterIoT.mdp.karma.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @class MyAdapter
 * @brief Adapter for binding Mission objects to a RecyclerView.
 *
 * This class extends RecyclerView.Adapter and manages the display of missions.
 * It supports selection tracking and provides methods for getting keys, positions,
 * and deleting items from the dataset.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    /** Tag for logging purposes. */
    private static final String TAG = "TAGListOfItems, MyAdapter";

    /** Reference to the dataset of missions. */
    private final MissionsDataset dataset;

    /** Selection tracker for keeping track of selected mission items. */
    private SelectionTracker<Long> selectionTracker;

    /**
     * @brief Constructor for MyAdapter.
     * @param dataset The MissionsDataset to bind to the RecyclerView.
     */
    public MyAdapter(MissionsDataset dataset) {
        super();
        Log.d(TAG, "MyAdapter() called");
        this.dataset = dataset;
    }

    /**
     * @brief Inflates the item layout and creates the ViewHolder.
     * @param parent The parent ViewGroup.
     * @param viewType Type of the view (unused).
     * @return A new MyViewHolder instance.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * @brief Binds data to the ViewHolder at a given position.
     * @param holder The MyViewHolder to bind data to.
     * @param position The position of the item in the dataset.
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Mission mission = dataset.getMissionAtPosition(position);
        Long missionKey = mission.getKey();
        boolean isMissionSelected = selectionTracker.isSelected(missionKey);

        Log.d(TAG, "onBindViewHolder() called for element in position " + position +
                ", Selected? = " + isMissionSelected);
        holder.bindValues(mission, isMissionSelected);
    }

    /**
     * @brief Returns the number of items in the dataset.
     * @return Size of the dataset.
     */
    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    /**
     * @brief Returns the key of the mission at a given position.
     * @param pos Position index.
     * @return Mission key.
     */
    public Long getKeyAtPosition(int pos) {
        return dataset.getKeyAtPosition(pos);
    }

    /**
     * @brief Returns the position of a mission in the dataset given its key.
     * @param searchedkey The key to search for.
     * @return Position index of the mission.
     */
    public int getPositionOfKey(Long searchedkey) {
        int position = dataset.getPositionOfKey(searchedkey);
        return position;
    }

    /**
     * @brief Sets the selection tracker for this adapter.
     * @param selectionTracker The SelectionTracker object to track item selections.
     */
    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    /**
     * @brief Deletes an item from the dataset and notifies the adapter.
     * @param position The position of the item to remove.
     */
    public void deleteItem(int position) {
        dataset.removeMissionAtPosition(position);
        notifyItemRemoved(position);
    }
}
