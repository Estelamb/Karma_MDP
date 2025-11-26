package masterIoT.mdp.karma.missions;

import masterIoT.mdp.karma.R;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @class MissionsDataset
 * @brief Represents the dataset containing all mission objects.
 *
 * This class follows the singleton pattern and holds an ArrayList of Mission objects.
 * It provides utility functions to access missions by position or key, add new missions,
 * and remove existing ones.
 */
public class MissionsDataset {

    /** Singleton instance. */
    private static MissionsDataset inst;

    /** Tag for logging purposes. */
    private static final String TAG = "TAGListOfMissions, Dataset";

    /** List storing all missions. */
    private List<Mission> listofmissions;

    /** Array of images for the predefined missions. */
    private int[] images = {R.drawable.ecocommuter,
            R.drawable.trashhero,
            R.drawable.recyclemaster,
            R.drawable.bagreuser,
            R.drawable.refillreuse,
            R.drawable.tuppersaver,
            R.drawable.fooddonor,
            R.drawable.bloodhero,
            R.drawable.booktoy,
            R.drawable.volunteerspirit,
            R.drawable.seniorhelper,
            R.drawable.communitywatcher};

    /** Array of titles for the predefined missions. */
    private String[] titles = {"Eco Commuter",
            "Trash Hero",
            "Recycle Master",
            "Bag Reuser",
            "Refill and Reuse",
            "Tupper Saver",
            "Food Donor",
            "Blood Hero",
            "Book and Toy Giver",
            "Volunteer Spirit",
            "Senior Helper",
            "Community Watcher"};

    /** Array of karma points corresponding to each mission. */
    private int[] karmaPoints = {15,
            20,
            10,
            10,
            10,
            10,
            25,
            40,
            20,
            30,
            20,
            15};

    /** Array of descriptions for each mission. */
    private String[] description = {"Use public transport, ride a bike, or walk instead of driving a car. A small change that greatly reduces your carbon footprint.",
            "Pick up litter from streets, parks, or beaches to help keep your community clean and beautiful.",
            "Separate your waste correctly for recycling plastics, paper, glass, and organic materials each in their place.",
            "Bring your own reusable bags or containers when shopping to avoid single-use plastics.",
            "Use a refillable water bottle instead of disposable plastic ones to save resources and reduce waste.",
            "Bring your own reusable container when getting take-out food to avoid unnecessary packaging.",
            "Donate food items to local food banks or community kitchens to support people in need.",
            "Donate blood to help save lives. Your small act of generosity can make a big difference.",
            "Donate books or toys in good condition to schools, shelters, or organizations that need them.",
            "Spend time volunteering for a charity, NGO, or animal shelter. Every hour you give matters.",
            "Offer assistance to elderly people, whether by shopping for them, spending time, or lending a hand.",
            "Report public issues like broken lights, damaged sidewalks, or waste problems to local authorities."};

    /** Array of map types corresponding to each mission. "none" if no map is associated. */
    private String[] mapType = {"none", "trash", "none", "none", "none", "none", "none", "blood", "giver", "none", "none", "none"};

    /**
     * @brief Private constructor initializing the predefined missions list.
     */
    MissionsDataset() {
        Log.d(TAG, "Dataset() called");
        listofmissions = new ArrayList<>();

        for (int i = 0; i < images.length; ++i) {
            listofmissions.add(new Mission(titles[i], images[i], karmaPoints[i], description[i], (long) i, mapType[i], false));
        }
    }

    /**
     * @brief Returns the singleton instance of the dataset.
     * @return MissionsDataset instance.
     */
    public static MissionsDataset getInstance() {
        if (inst == null) {
            inst = new MissionsDataset();
        }
        return inst;
    }

    /**
     * @brief Returns the number of missions in the dataset.
     * @return Number of missions.
     */
    public int getSize() {
        return listofmissions.size();
    }

    /**
     * @brief Returns the mission at a given position.
     * @param pos Position index.
     * @return Mission object at the specified position.
     */
    Mission getMissionAtPosition(int pos) {
        return listofmissions.get(pos);
    }

    /**
     * @brief Returns the key of the mission at a given position.
     * @param pos Position index.
     * @return Key of the mission.
     */
    Long getKeyAtPosition(int pos) {
        return listofmissions.get(pos).getKey();
    }

    /**
     * @brief Finds the position of a mission in the dataset given its key.
     * @param searchedkey Key to search for.
     * @return Position index of the mission, or -1 if not found.
     */
    public int getPositionOfKey(Long searchedkey) {
        int position = listofmissions.indexOf(new Mission("placeholder",0, 0, "descrption", searchedkey, "none", false));
        return position;
    }

    /**
     * @brief Adds a mission to the dataset.
     * @param mission Mission to add.
     */
    public void addMission(Mission mission) {
        listofmissions.add(mission);
    }

    /**
     * @brief Removes the mission at a given position.
     * @param i Position index of the mission to remove.
     */
    void removeMissionAtPosition(int i) {
        listofmissions.remove(i);
    }

    /**
     * @brief Removes a mission from the dataset given its key.
     * @param key Key of the mission to remove.
     */
    public void removeMissionWithKey(Long key) {
        removeMissionAtPosition(getPositionOfKey(key));
    }
}
