/**
 * @file MyMissionKeyProvider.java
 * @brief Provides stable selection keys for mission items in a RecyclerView.
 *
 * This class is used by the RecyclerView Selection library to map
 * positions to unique keys (Long) for selection purposes. It allows
 * SelectionTracker to identify and track selected mission items.
 */

package masterIoT.mdp.karma.missions;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @class MyMissionKeyProvider
 * @brief Maps RecyclerView positions to stable Long keys for selection.
 *
 * This class extends ItemKeyProvider<Long> and provides SelectionTracker
 * with a way to retrieve a unique key for each mission item at a given position,
 * and vice versa.
 */
public class MyMissionKeyProvider extends ItemKeyProvider<Long> {

    /** Tag for logging purposes */
    private static final String TAG = "TAGListOfItems, MyItemKeyProvider";

    /** Reference to the RecyclerView containing mission items */
    RecyclerView recView;

    /**
     * @brief Constructor for MyMissionKeyProvider.
     *
     * @param scope Scope of the provider (SCOPE_MAPPED or SCOPE_CACHED).
     * @param rv RecyclerView that contains the mission items.
     */
    @SuppressLint("LongLogTag")
    public MyMissionKeyProvider(int scope, RecyclerView rv) {
        super(scope);
        recView = rv;
        Log.d(TAG, "MyMissionKeyProvider() called");
    }

    /**
     * @brief Returns the unique key associated with a given position.
     *
     * @param position Position of the item in the RecyclerView.
     * @return Unique Long key for the item at that position.
     */
    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public Long getKey(int position) {
        Log.d(TAG, "getKey() called for position " + position);
        return (((MyAdapter) recView.getAdapter()).getKeyAtPosition(position));
    }

    /**
     * @brief Returns the position of the item corresponding to the given key.
     *
     * @param key Unique Long key of the item.
     * @return Position of the item in the RecyclerView.
     */
    @SuppressLint("LongLogTag")
    @Override
    public int getPosition(@NonNull Long key) {
        Log.d(TAG, "getPosition() called for key " + key);
        return (((MyAdapter) recView.getAdapter()).getPositionOfKey(key));
    }
}
