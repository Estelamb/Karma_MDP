package masterIoT.mdp.karma.missions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import masterIoT.mdp.karma.R;
import masterIoT.mdp.karma.MapActivity;

public class MissionInfo {

    private final Context context;
    private final Mission mission;
    TextView title, karma, description;
    private ImageView previewImage;

    public MissionInfo(Context context, Mission mission) {
        this.context = context;
        this.mission = mission;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.mission_info, null);
        builder.setView(dialogView);

        title = dialogView.findViewById(R.id.missionName);
        karma = dialogView.findViewById(R.id.karmaPoints);
        description = dialogView.findViewById(R.id.Description);
        previewImage = dialogView.findViewById(R.id.imageView3);

        title.setText(mission.getTitle());
        karma.setText(String.valueOf(mission.getKarmaPoints()));
        previewImage.setImageResource(mission.getImage());
        description.setText(mission.getDescription());

        if(!mission.getMapType().equals("none")) {
            builder.setPositiveButton("Map", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("mapType", mission.getMapType());
                context.startActivity(intent);
            });
        }

        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
