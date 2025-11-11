package masterIoT.mdp.karma;

import static java.lang.Double.NaN;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import masterIoT.mdp.karma.modelo.WaypointItem;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    ExecutorService es;
    GoogleMap mMap;

    TextView tv;
    ProgressBar pg;
    String response;
    String mapType = "";
    Map<String, LatLng> waypointsMap;
    SupportMapFragment mapFragment;
    private ClusterManager<WaypointItem> clusterManager;
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

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        if(!strUrl.isEmpty()) {
            DescargaFichero loadURLContents = new DescargaFichero(handler, strUrl);
            es.execute(loadURLContents);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            string_result = msg.getData().getString("response");
            try {

                if (string_result != null) {
                    if (mapType.equals("trash")) waypointsMap = parseJsonTrash(string_result);
                    else waypointsMap = parseJsonBookandBlood(string_result);

                }

                if (mMap != null) {
                    for (String id : waypointsMap.keySet()) {
                        mMap.addMarker(new MarkerOptions().position(waypointsMap.get(id)).title(id));
//                        WaypointItem item = new WaypointItem(waypointsMap.get(id), id, "");
//                        clusterManager.addItem(item);
                    }
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    pg.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
//                    clusterManager.cluster();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<>(this, mMap);
        LatLng ui = new LatLng(40.403577814444176, -3.6724086076957154 );
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ui));
        UiSettings uis = mMap.getUiSettings();
        uis.setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ui, 15));
        if (waypointsMap!=null) {
            for (String id : waypointsMap.keySet()) {
                mMap.addMarker(new MarkerOptions().position(waypointsMap.get(id)).title(id));
//                WaypointItem item = new WaypointItem(waypointsMap.get(id), id, "");
//                clusterManager.addItem(item);
            }
            mapFragment.getView().setVisibility(View.VISIBLE);
            pg.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
//            clusterManager.cluster();

        }
    }

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