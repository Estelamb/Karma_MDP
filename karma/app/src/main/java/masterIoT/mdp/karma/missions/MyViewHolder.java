package masterIoT.mdp.karma.missions;

import masterIoT.mdp.karma.R;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @class MyViewHolder
 * @brief Holds references to views for a mission item and binds mission data.
 *
 * Each ViewHolder contains an ImageView for the mission image, and TextViews
 * for the title and karma points. The bindValues() method updates these views
 * with the mission's data and adjusts text color if the item is selected.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    /** ImageView for the mission's image */
    ImageView image;

    /** TextView for the mission's title */
    TextView title;

    /** TextView for displaying the mission's karma points */
    TextView karmaPoints;

    /** Tag for logging */
    private static final String TAG = "TAGListOfMissions, MyViewHolder";

    /**
     * @brief Constructor for MyViewHolder.
     *
     * Initializes the views for a mission item.
     *
     * @param itemView The view corresponding to a single mission item.
     */
    public MyViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.missionImage);
        title = itemView.findViewById(R.id.missionTitle);
        karmaPoints = itemView.findViewById(R.id.missionSubtitle);
    }

    /**
     * @brief Binds a Mission's data to the views in this ViewHolder.
     *
     * @param mission The mission object containing data.
     * @param isSelected True if the mission item is currently selected; used to
     *                   change the text color.
     */
    void bindValues(Mission mission, Boolean isSelected) {
        image.setImageResource(mission.getImage());
        title.setText(mission.getTitle());
        karmaPoints.setText(Integer.toString(mission.getKarmaPoints()) + " Karma Points");
        if(isSelected) {
            title.setTextColor(Color.BLUE);
            karmaPoints.setTextColor(Color.BLUE);
        } else {
            title.setTextColor(Color.BLACK);
            karmaPoints.setTextColor(Color.BLACK);
        }
    }

}
