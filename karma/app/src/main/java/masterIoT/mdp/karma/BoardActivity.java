/**
 * @file BoardActivity.java
 * @brief Displays the leaderboard screen including karma ranking.
 * @details This activity loads user karma values from SharedPreferences,
 *          displays them in a horizontal bar chart, highlights the current user,
 *          and allows navigation to the profile screen.
 */

package masterIoT.mdp.karma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class BoardActivity
 * @brief Handles the leaderboard activity screen.
 * @details Loads and displays user karma values using a horizontal bar chart and
 *          allows profile navigation.
 */
public class BoardActivity extends AppCompatActivity {

    /** Profile button */
    private ImageView bProfile;

    /** TextView showing current user's karma */
    private TextView tvKarma;

    /** MQTT client instance */
    private MQTT mqttClient;

    /** Horizontal bar chart displaying karma ranking */
    private HorizontalBarChart graf_horizontal;

    /**
     * @brief Called when the activity is created.
     * @param savedInstanceState Saved instance state bundle.
     */
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

        bProfile = findViewById(R.id.imageView);
        tvKarma = findViewById(R.id.tvKarmaPoints);
        graf_horizontal = findViewById(R.id.horizontalBarChart);

        SharedPreferences prefs = getSharedPreferences("KarmaPoints", Context.MODE_PRIVATE);
        int karmaPoints = prefs.getInt("totalKarma", 0);
        tvKarma.setText(String.valueOf(karmaPoints));

        setupMQTT();
        setupChart();

        // Profile button opens the ProfileActivity
        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        loadChartData();
    }

    /**
     * @brief Configures visual and interactive settings for the horizontal bar chart.
     */
    private void setupChart() {
        graf_horizontal.setDrawBarShadow(false);
        graf_horizontal.setDrawValueAboveBar(true);
        graf_horizontal.getDescription().setEnabled(false);
        graf_horizontal.setPinchZoom(false);
        graf_horizontal.setDrawGridBackground(false);
        graf_horizontal.getLegend().setEnabled(false);

        XAxis xAxis = graf_horizontal.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5, true);

        YAxis leftAxis = graf_horizontal.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = graf_horizontal.getAxisRight();
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMinimum(0f);

        graf_horizontal.animateY(1000);
    }

    /**
     * @brief Loads karma data for all users and populates the chart.
     * @details Reads SharedPreferences "UsersKarma", sorts users by value,
     *          highlights current user and refreshes the chart view.
     */
    private void loadChartData() {
        Map<String, ?> MapaDatos = getSharedPreferences("UsersKarma", Context.MODE_PRIVATE).getAll();

        if (MapaDatos.isEmpty()) {
            return;
        }

        List<Map.Entry<String, ?>> sortedEntries = new ArrayList<>(MapaDatos.entrySet());
        sortedEntries.sort((a, b) -> {
            float valA = convertToFloat(a.getValue());
            float valB = convertToFloat(b.getValue());
            return Float.compare(valA, valB);
        });

        String[] labels = new String[sortedEntries.size()];
        ArrayList<BarEntry> entries = new ArrayList<>();

        SharedPreferences appPrefs = getSharedPreferences("KarmaAppPrefs", Context.MODE_PRIVATE);
        String currentUsername = appPrefs.getString("username", "");

        int count = 0;
        for (Map.Entry<String, ?> entry : sortedEntries) {
            labels[count] = entry.getKey();
            float value = convertToFloat(entry.getValue());
            entries.add(new BarEntry(count, value));
            count++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Puntos de Karma");

        int[] colors = new int[entries.size()];
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equals(currentUsername)) {
                colors[i] = Color.parseColor("#C020EE");  // highlight current user
            } else {
                colors[i] = Color.parseColor("#8EE962");
            }
        }
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));

        graf_horizontal.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        graf_horizontal.getXAxis().setLabelCount(labels.length);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        graf_horizontal.setData(barData);
        graf_horizontal.invalidate();
    }

    /**
     * @brief Converts an object from SharedPreferences to float.
     * @param value Object to convert.
     * @return Corresponding float value or 0 if conversion fails.
     */
    private float convertToFloat(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        } else if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                return 0f;
            }
        }
        return 0f;
    }

    /**
     * @brief Initializes and connects the MQTT client.
     */
    private void setupMQTT() {
        mqttClient = MQTT.getInstance(this);
        mqttClient.connect();
    }
}
