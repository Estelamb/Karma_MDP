package masterIoT.mdp.karma;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @brief Main activity of the Karma app.
 *
 * Displays random motivational messages every 10 seconds.
 */
public class MainActivity extends AppCompatActivity {

    /** @brief TextView that displays motivational messages */
    private TextView mensajesMotivados;

    /** @brief Handler for scheduling periodic updates */
    private Handler handler;

    /** @brief Runnable that updates the TextView with a random message */
    private Runnable runnable;

    /** @brief List of motivational messages */
    private List<String> mensajes;

    /** @brief Random generator for selecting messages */
    private Random random;

    /**
     * @brief Called when the activity is first created.
     *
     * Sets up the UI and starts the periodic message updates.
     *
     * @param savedInstanceState Previous state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mensajesMotivados = findViewById(R.id.txvwMensajesPositivos);
        mensajes = Arrays.asList(
                "Believe in yourself and all that you are.",
                "Every day is a new beginning.",
                "You are stronger than you think.",
                "Dream big and dare to fail.",
                "Keep going — you’re doing great!",
                "The best time for new beginnings is now.",
                "Be the energy you want to attract.",
                "Your only limit is your mind.",
                "Small steps every day lead to big results.",
                "Stay positive, work hard, make it happen."
        );
        random = new Random();
        handler = new Handler();
        runnable = new Runnable() {
            /**
             * @brief Updates the TextView with a random motivational message
             *        and schedules the next update.
             */
            @Override
            public void run() {
                String mensaje = mensajes.get(random.nextInt(mensajes.size()));
                mensajesMotivados.setText(mensaje);
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    /**
     * @brief Called when the activity is destroyed.
     *
     * Stops the handler to avoid memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
