package masterIoT.mdp.karma;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;

public class BoardActivity extends AppCompatActivity {
    private ImageView bProfile;
    private TextView tvKarma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bProfile=findViewById(R.id.imageView);
        tvKarma=findViewById(R.id.tvKarmaPoints);
        SharedPreferences prefs=getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        int karmaPoints = prefs.getInt("totalKarma", 0);
        tvKarma.setText(String.valueOf(karmaPoints));
        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}