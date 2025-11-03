package masterIoT.mdp.karma.missions;
import masterIoT.mdp.karma.R;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    ImageView image;
    TextView title;
    TextView karmaPoints;

    private static final String TAG = "TAGListOfMissions, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.missionImage);
        title = itemView.findViewById(R.id.missionTitle);
        karmaPoints = itemView.findViewById(R.id.missionSubtitle);
    }

    void bindValues(Mission mission, Boolean isSelected) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
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