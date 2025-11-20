package masterIoT.mdp.karma.missions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import masterIoT.mdp.karma.R;
import masterIoT.mdp.karma.MapActivity;
import masterIoT.mdp.karma.MQTT;

public class MissionInfo {

    private final Context context;
    private final Mission mission;
    TextView title, karma, description;
    Button mapButton;
    private ImageView previewImage;
    private MQTT mqttClient;

    public MissionInfo(Context context, Mission mission) {
        this.context = context;
        this.mission = mission;
        this.mqttClient = MQTT.getInstance(context);
        this.mqttClient.connect();
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.mission_info, null);
        builder.setView(dialogView);

        title = dialogView.findViewById(R.id.missionName);
        karma = dialogView.findViewById(R.id.karmaPoints);
        description = dialogView.findViewById(R.id.Description);
        previewImage = dialogView.findViewById(R.id.imageView3);
        mapButton = dialogView.findViewById(R.id.button);

        title.setText(mission.getTitle());
        karma.setText(String.valueOf(mission.getKarmaPoints()));
        previewImage.setImageResource(mission.getImage());
        description.setText(mission.getDescription());

        if(!mission.getMapType().equals("none")) {
            builder.setNeutralButton("Map", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("mapType", mission.getMapType());
                context.startActivity(intent);
            });
        }
        builder.setPositiveButton("Complete", (dialog, which) -> {
            dialog.dismiss();
            SharedPreferences prefs= context.getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
            int karma = prefs.getInt("totalKarma", 0);
            int points = mission.getKarmaPoints();
            karma=karma+points;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalKarma", karma);
            editor.apply();
            publicarKarma(points,karma);
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void publicarKarma(int points, int karma){
        String username=getUsername();
        mqttClient.publish("app/addPuntos",String.valueOf(points), false);
        String message= username+":"+karma;
        mqttClient.publish("app/users/"+username+"/karmaTotal", message,true);
    }

    private String getUsername() {
        SharedPreferences prefs = context.getSharedPreferences("KarmaAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "Usuario"); // "Usuario" es valor por defecto
    }

}
