package masterIoT.mdp.karma.missions;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

final public class MyMissionDetailsLookup  extends ItemDetailsLookup<Long> {

    private static final String TAG = "TAGListOfMissions, MyMissionDetailsLookup";

    private final RecyclerView mRecyclerView;

    @SuppressLint("LongLogTag")
    public MyMissionDetailsLookup(RecyclerView recyclerView) {
        Log.d(TAG, "MyMissionDetailsLookup() called");
        mRecyclerView = recyclerView;
    }

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
                        return (positionOfTheHolder);
                    }
                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return (keyOfTheHolder);
                    }
                };

                return missionDetails;
            }
        }
        return null;
    }

}