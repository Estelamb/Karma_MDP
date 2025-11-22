/**
 * @file MissionInfo.java
 * @brief Displays detailed information about a mission and allows interaction.
 *
 * This class shows a dialog with the mission's title, description, karma points,
 * and image. It allows the user to mark the mission as complete, update karma points,
 * and optionally open a map related to the mission.
 */

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

/**
 * @class MissionInfo
 * @brief Handles the UI and interaction for displaying mission details.
 *
 * This class creates a dialog to show mission information and allows the user
 * to complete the mission or view a related map if applicable.
 */
public class MissionInfo {

    /** Application context used for UI and SharedPreferences. */
    private final Context context;

    /** The mission whose information is displayed. */
    private final Mission mission;

    /** TextView for the mission title. */
    TextView title;

    /** TextView for the mission karma points. */
    TextView karma;

    /** TextView for the mission description. */
    TextView description;

    /** Button to open the map related to the mission. */
    Button mapButton;

    /** ImageView to show a preview image of the mission. */
    private ImageView previewImage;

    /** MQTT client instance used for publishing karma updates. */
    private MQTT mqttClient;

    /**
     * @brief Constructor initializes the MissionInfo with context and mission.
     * @param context Application context.
     * @param mission Mission object to display.
     */
    public MissionInfo(Context context, Mission mission) {
        this.context = context;
        this.mission = mission;
        this.mqttClient = MQTT.getInstance(context);
        this.mqttClient.connect();
    }

    /**
     * @brief Shows a dialog with the mission information.
     *
     * The dialog displays the title, description, image, and karma points.
     * It also allows completing the mission and optionally viewing the related map.
     */
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

    /**
     * @brief Publishes updated karma points for the user via MQTT.
     * @param points Points earned from completing the mission.
     * @param karma Total updated karma points.
     */
    private void publicarKarma(int points, int karma){
        String username=getUsername();
        mqttClient.publish("app/addPuntos",String.valueOf(points), false);
        String message= username+":"+karma;
        mqttClient.publish("app/users/"+username+"/karmaTotal", message,true);
    }

    /**
     * @brief Returns the username stored in SharedPreferences.
     * @return Username as a String.
     */
    private String getUsername() {
        SharedPreferences prefs = context.getSharedPreferences("KarmaAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "Usuario"); // Default value "Usuario"
    }

}
