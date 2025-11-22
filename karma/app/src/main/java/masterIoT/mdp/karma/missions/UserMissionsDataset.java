package masterIoT.mdp.karma.missions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @class UserMissionsDataset
 * @brief Holds missions created by the user.
 *
 * Filters the main MissionsDataset to include only missions with user flag set to true.
 * Provides methods for accessing size, missions by position, keys, and removing or adding missions.
 */
public class UserMissionsDataset extends MissionsDataset {

    /** Reference to the global missions dataset */
    private MissionsDataset missionsDataset = MissionsDataset.getInstance();

    /** List of missions created by the user */
    private List<Mission> userMissions;

    /**
     * @brief Constructor for UserMissionsDataset.
     *
     * Initializes the list of user missions by filtering the global dataset.
     */
    public UserMissionsDataset() {
        userMissions = new ArrayList<>();
        for (int i = 0; i < missionsDataset.getSize(); i++) {
            if (missionsDataset.getMissionAtPosition(i).getUser()) {
                userMissions.add(missionsDataset.getMissionAtPosition(i));
            }
        }
        Log.d("", "UserMissionsDataset() called");
    }

    /**
     * @brief Returns the number of user-created missions.
     * @return Number of missions.
     */
    int getSize() {
        return userMissions.size();
    }

    /**
     * @brief Returns the mission at a given position.
     * @param pos The position in the list.
     * @return Mission object at the specified position.
     */
    Mission getMissionAtPosition(int pos) {
        return userMissions.get(pos);
    }

    /**
     * @brief Returns the key of the mission at a given position.
     * @param pos The position in the list.
     * @return Key of the mission.
     */
    Long getKeyAtPosition(int pos) {
        return (userMissions.get(pos).getKey());
    }

    /**
     * @brief Returns the position of a mission with a given key.
     * @param searchedkey The key to search for.
     * @return Position of the mission with the specified key.
     */
    public int getPositionOfKey(Long searchedkey) {
        int position = userMissions.indexOf(new Mission("placeholder", 0, 0, "descrption", searchedkey, "none", false));
        return position;
    }

    /**
     * @brief Adds a new mission to the user missions dataset.
     * @param mission The mission to add.
     */
    void addMission(Mission mission) { userMissions.add(mission); }

    /**
     * @brief Removes the mission at a specific position.
     * @param i Position of the mission to remove.
     */
    void removeMissionAtPosition(int i) { userMissions.remove(i); }

    /**
     * @brief Removes a mission by its key.
     * @param key Key of the mission to remove.
     */
    public void removeMissionWithKey(Long key) {
        removeMissionAtPosition(getPositionOfKey(key));
    }
}
