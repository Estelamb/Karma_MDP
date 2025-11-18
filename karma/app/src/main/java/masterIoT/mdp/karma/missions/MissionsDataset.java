package masterIoT.mdp.karma.missions;
import masterIoT.mdp.karma.R;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MissionsDataset {

    // This dataset is a list of Missions
    private static MissionsDataset inst;
    private static final String TAG = "TAGListOfMissions, Dataset";
    private List<Mission> listofmissions;
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

    private String[] mapType = {"none", "trash", "none", "none", "none", "none", "none", "blood", "giver", "none", "none", "none"};

    MissionsDataset() {
        Log.d(TAG, "Dataset() called");
        listofmissions = new ArrayList<>();

        for (int i = 0; i < images.length; ++i) {
            listofmissions.add(new Mission(titles[i], images[i], karmaPoints[i], description[i], (long) i, mapType[i], false));
        }
    }

    public static MissionsDataset getInstance() {
        if (inst == null) {
            inst = new MissionsDataset();
        }
        return inst;
    }
    int getSize() {
        return listofmissions.size();
    }

    Mission getMissionAtPosition(int pos) {
        return listofmissions.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (listofmissions.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        // The following works because in Item, the method "equals" is overriden to compare only keys:
        //int position = listofmissions.indexOf(new Item("placeholder", "placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        int position = listofmissions.indexOf(new Mission("placeholder",0, 0, "descrption", searchedkey, "none", false));
        return position;
    }

    void addMission(Mission mission) { listofmissions.add(mission); }

    void removeMissionAtPosition(int i) { listofmissions.remove(i); }

    public void removeMissionWithKey(Long key) {
        removeMissionAtPosition(getPositionOfKey(key));
    }

}
