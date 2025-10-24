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
 * MainActivity is the entry point of the Karma app.
 * <p>
 * It displays a motivational message that changes automatically every 10 seconds.
 * The messages are randomly selected from a predefined list.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /** TextView used to display motivational messages on screen. */
    private TextView mensajesMotivados;

    /** Handler used to schedule and repeat the message updates. */
    private Handler handler;

    /** Runnable that updates the motivational message every few seconds. */
    private Runnable runnable;

    /** List containing all motivational messages available for display. */
    private List<String> mensajes;

    /** Random generator used to pick messages from the list. */
    private Random random;

    /**
     * Called when the activity is first created.
     * <p>
     * This method initializes the user interface, prepares the list of motivational messages,
     * and starts a repeating task that changes the message every 10 seconds.
     * </p>
     *
     * @param savedInstanceState The previously saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configure window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components and data
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

        // Define the repeating task that updates the motivational message
        runnable = new Runnable() {
            @Override
            public void run() {
                // Pick a random message and display it
                String mensaje = mensajes.get(random.nextInt(mensajes.size()));
                mensajesMotivados.setText(mensaje);

                // Schedule the next update in 10 seconds (10,000 ms)
                handler.postDelayed(this, 10000);
            }
        };

        // Start the first update immediately
        handler.post(runnable);
    }

    /**
     * Called when the activity is being destroyed.
     * <p>
     * This method stops the repeating message updates to prevent memory leaks.
     * </p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
