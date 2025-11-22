/**
 * @file WaypointItem.java
 * @brief Represents a waypoint used for clustering markers on Google Maps.
 *
 * This class implements the ClusterItem interface from the Google Maps
 * Android Utility Library, enabling map markers to be grouped into clusters.
 * Each waypoint contains a geographic position, a title, and a snippet.
 */

package masterIoT.mdp.karma.modelo;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * @class WaypointItem
 * @brief Data model representing a waypoint marker that can be clustered on a map.
 *
 * A WaypointItem stores the required information for a map marker, including its
 * geographic coordinates, title, and optional description. It is used by the
 * Google Maps clustering system to visually group nearby markers.
 */
public class WaypointItem implements ClusterItem {

    /** Geographic position (latitude & longitude) of the waypoint. */
    private final LatLng position;

    /** Title displayed when the marker is tapped. */
    private final String title;

    /** Additional text shown in the marker info window. */
    private final String snippet;

    /**
     * @brief Constructs a new waypoint marker.
     *
     * @param position Latitude and longitude of the waypoint.
     * @param title Title shown for the marker.
     * @param snippet Additional description for the marker.
     */
    public WaypointItem(LatLng position, String title, String snippet) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
    }

    /**
     * @brief Returns the geographic position of the waypoint.
     * @return LatLng coordinates.
     */
    @Override
    public LatLng getPosition() {
        return position;
    }

    /**
     * @brief Returns the title for this marker.
     * @return Marker title string.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @brief Returns the snippet associated with this marker.
     * @return Marker snippet text.
     */
    @Override
    public String getSnippet() {
        return snippet;
    }

    /**
     * @brief Returns the z-index of this marker.
     *
     * This implementation always returns 0 as clustering handles marker stacking.
     *
     * @return Float value representing z-index (default is 0).
     */
    @Nullable
    @Override
    public Float getZIndex() {
        return 0f;
    }
}
