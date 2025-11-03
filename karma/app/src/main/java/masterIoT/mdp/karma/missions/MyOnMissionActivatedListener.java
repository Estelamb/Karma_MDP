package masterIoT.mdp.karma.missions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

public class MyOnMissionActivatedListener implements OnItemActivatedListener<Long> {

    private static final String TAG = "TAGListOfItems, MyOnItemActivatedListener";

    private final Context context;
    private MissionsDataset dataset; // reference to the dataset, so that the activated item's data can be accessed if necessary

    public MyOnMissionActivatedListener(Context context, MissionsDataset ds) {
        this.context = context;
        this.dataset = ds;
    }

    // ------ Implementation of methods ------ //

    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails itemdetails,
                                   @NonNull MotionEvent e) {
        // From [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]:
        // "Called when an item is "activated". An item is activated, for example,
        // when no selection exists and the user taps an item with her finger,
        // or double clicks an item with a pointing device like a Mouse."

//        Log.d(TAG, "Clicked item with position = " + itemdetails.getPosition()
//                + " and key = " + itemdetails.getSelectionKey());
//
//        Intent i = new Intent(context, SecondActivity.class);
//        i.putExtra("text", "Clicked item with position = " + itemdetails.getPosition()
//                + " and key = " + itemdetails.getSelectionKey());
//        context.startActivity(i);


//        Long key =(Long) missiondetails.getSelectionKey();
//        int pos = dataset.getPositionOfKey(key);
//        if (pos >= 0) {
//            Mission mission = dataset.getMissionAtPosition(pos);
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mission.getURI()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            return true;
//        }
        return false;
    }
}
