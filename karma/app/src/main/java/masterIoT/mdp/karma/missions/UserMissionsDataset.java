package masterIoT.mdp.karma.missions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserMissionsDataset extends MissionsDataset {

    private MissionsDataset missionsDataset = MissionsDataset.getInstance();
    private List<Mission> userMissions;

    public UserMissionsDataset() {
        userMissions = new ArrayList<>();
        for (int i = 0; i < missionsDataset.getSize(); i++) {
            if (missionsDataset.getMissionAtPosition(i).getUser()) {
                userMissions.add(missionsDataset.getMissionAtPosition(i));
            }
        }
        Log.d("", "UserMissionsDataset() called");
    }

    int getSize() {
        return userMissions.size();
    }

    Mission getMissionAtPosition(int pos) {
        return userMissions.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (userMissions.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        // The following works because in Item, the method "equals" is overriden to compare only keys:
        //int position = listofmissions.indexOf(new Item("placeholder", "placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        int position = userMissions.indexOf(new Mission("placeholder",0, 0, "descrption", searchedkey, "none", false));
        return position;
    }

    void addMission(Mission mission) { userMissions.add(mission); }

    void removeMissionAtPosition(int i) { userMissions.remove(i); }

    public void removeMissionWithKey(Long key) {
        removeMissionAtPosition(getPositionOfKey(key));
    }
}
