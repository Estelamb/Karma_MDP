package masterIoT.mdp.karma.missions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private final ActivityResultLauncher<Intent> galleryLauncher;
    private final ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap selectedBitmap;
    private ImageView previewImage;

    public AddMission(Context context, MissionsDataset dataset,
                      RecyclerView.Adapter<?> adapter,
                      ActivityResultLauncher<Intent> galleryLauncher,
                      ActivityResultLauncher<Intent> cameraLauncher) {
        this.context = context;
        this.dataset = dataset;
        this.adapter = adapter;
        this.galleryLauncher = galleryLauncher;
        this.cameraLauncher = cameraLauncher;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_mission, null);
        builder.setView(dialogView);

        EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        EditText inputKarma = dialogView.findViewById(R.id.inputKarma);
        EditText inputDescription = dialogView.findViewById(R.id.inputDescription);
        previewImage = dialogView.findViewById(R.id.previewImage);
        Button buttonGallery = dialogView.findViewById(R.id.buttonGallery);
        Button buttonCamera = dialogView.findViewById(R.id.buttonCamera);

        buttonGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        buttonCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            int karmaPoints = 0;
            try {
                karmaPoints = Integer.parseInt(inputKarma.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            int imageRes = R.drawable.ecocommuter;
            Mission newMission = new Mission(title, imageRes, karmaPoints, description, (long) dataset.getSize());
            dataset.addMission(newMission);
            adapter.notifyItemInserted(dataset.getSize() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
