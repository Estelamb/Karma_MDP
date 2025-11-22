package masterIoT.mdp.karma;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import masterIoT.mdp.karma.modelo.WaypointItem;

/**
 * @class MapActivity
 * @brief Loads waypoint data from a remote JSON source and displays it on a Google Map.
 *
 * The Activity determines the dataset based on the "mapType" extra. Data is downloaded
 * asynchronously using FileDownload and populated on the map once ready.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    /** Executor service for background operations (JSON download). */
    ExecutorService es;

    /** GoogleMap instance once map is ready. */
    GoogleMap mMap;

    /** Label displayed while loading data. */
    TextView tv;

    /** Progress bar shown during data download and parsing. */
    ProgressBar pg;

    /** Response string containing JSON data downloaded from the remote source. */
    String response;

    /** Type of map to display: "trash", "blood" or "giver". */
    String mapType = "";

    /** Map of waypoint titles to their geographic coordinates. */
    Map<String, LatLng> waypointsMap;

    /** Fragment that contains and displays the Google Map UI. */
    SupportMapFragment mapFragment;

    /** Cluster manager for grouping map marker items (currently unused). */
    private ClusterManager<WaypointItem> clusterManager;

    /**
     * @brief Initializes the Activity, UI, map fragment, resolves dataset type, and triggers download.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv = findViewById(R.id.loadingmap);
        pg = findViewById(R.id.progressBar);

        es = Executors.newSingleThreadExecutor();

        String strUrl = "";

        // Initialize map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Determine which dataset to use
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mapType = extras.getString("mapType", "");
        }

        switch (mapType) {
            case "trash":
                strUrl = "https://datos.madrid.es/egob/catalogo/300096-25-mobiliario-papeleras.json";
                break;
            case "blood":
                strUrl = "https://datos.madrid.es/egob/catalogo/212769-0-atencion-medica.json";
                break;
            case "giver":
                strUrl = "https://datos.madrid.es/egob/catalogo/201747-0-bibliobuses-bibliotecas.json";
                break;
        }

        // Launch asynchronous download
        if (!strUrl.isEmpty()) {
            FileDownload loadURLContents = new FileDownload(handler, strUrl);
            es.execute(loadURLContents);
        }
    }

    /**
     * @brief Handler invoked when the background downloader finishes.
     *
     * Receives JSON, parses it into waypoint coordinates, and updates the map if already initialized.
     *
     * @param msg Message containing "response" String with the downloaded JSON or error text.
     */
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String string_result = msg.getData().getString("response");

            try {
                if (string_result != null) {
                    if (mapType.equals("trash"))
                        waypointsMap = parseJsonTrash(string_result);
                    else
                        waypointsMap = parseJsonBookandBlood(string_result);
                }

                if (mMap != null) {
                    // Display markers on the map
                    for (String id : waypointsMap.keySet()) {
                        mMap.addMarker(new MarkerOptions().position(waypointsMap.get(id)).title(id));
                    }
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    pg.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @brief Callback executed when the Google Map instance is fully ready.
     *
     * Sets up UI settings, initial camera position, and displays markers if already parsed.
     *
     * @param googleMap Ready-to-use map instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<>(this, mMap);

        LatLng ui = new LatLng(40.403577814444176, -3.6724086076957154);

        UiSettings uis = mMap.getUiSettings();
        uis.setZoomControlsEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ui, 15));

        // If data was already parsed before map initialization, add markers now
        if (waypointsMap != null) {
            for (String id : waypointsMap.keySet()) {
                mMap.addMarker(new MarkerOptions().position(waypointsMap.get(id)).title(id));
            }
            mapFragment.getView().setVisibility(View.VISIBLE);
            pg.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        }
    }

    /**
     * @brief Parses JSON for "blood" and "giver" map types (dataset with @graph array).
     *
     * @param Response Raw JSON string obtained from Madrid Open Data.
     * @return Map of waypoint titles to their geographic LatLng positions.
     * @throws JSONException If parsing fails.
     */
    private Map<String, LatLng> parseJsonBookandBlood(String Response) throws JSONException {
        Map<String, LatLng> results = new HashMap<>();

        JSONObject root = new JSONObject(Response);
        JSONArray graph = root.optJSONArray("@graph");

        for (int i = 0; i < graph.length(); i++) {
            JSONObject node = graph.optJSONObject(i);
            if (node == null) continue;

            String title = node.optString("title");
            JSONObject loc = node.optJSONObject("location");
            if (loc == null) continue;

            double latObj = loc.optDouble("latitude");
            double lonObj = loc.optDouble("longitude");

            results.put(title, new LatLng(latObj, lonObj));
        }
        return results;
    }

    /**
     * @brief Parses JSON for "trash" map type (simple array of objects).
     *
     * @param Response Raw JSON array string from Madrid Open Data.
     * @return Map of IDs to LatLng coordinates.
     * @throws JSONException If JSON is malformed.
     */
    private Map<String, LatLng> parseJsonTrash(String Response) throws JSONException {
        Map<String, LatLng> results = new HashMap<>();
        JSONArray graph = new JSONArray(Response);

        for (int i = 0; i < graph.length(); i++) {
            JSONObject node = graph.getJSONObject(i);
            if (node == null) continue;

            String title = node.optString("ID");

            double latObj = node.optDouble("LATITUD");
            double lonObj = node.optDouble("LONGITUD");

            results.put(title, new LatLng(latObj, lonObj));
        }

        return results;
    }
}
