package masterIoT.mdp.karma.missions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import masterIoT.mdp.karma.R;
public class AddMission {

    private final Context context;
    private final MissionsDataset dataset;
    private final RecyclerView.Adapter<?> adapter;
    EditText inputTitle, inputKarma, inputDescription;
    private ImageView previewImage;

    public AddMission(Context context, MissionsDataset dataset,
                      RecyclerView.Adapter<?> adapter) {
        this.context = context;
        this.dataset = dataset;
        this.adapter = adapter;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_mission, null);
        builder.setView(dialogView);

        inputTitle = dialogView.findViewById(R.id.inputTitle);
        inputKarma = dialogView.findViewById(R.id.inputKarma);
        inputDescription = dialogView.findViewById(R.id.inputDescription);
        previewImage = dialogView.findViewById(R.id.previewImage);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            int karmaPoints = 0;
            try {
                karmaPoints = Integer.parseInt(inputKarma.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            int imageRes = R.drawable.addmission;
            Mission newMission = new Mission(title, imageRes, karmaPoints, description, (long) dataset.getSize(), "", true);
            dataset.addMission(newMission);
            adapter.notifyItemInserted(dataset.getSize() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
