package masterIoT.mdp.karma.missions;

import android.net.Uri;

/**
 * @class Mission
 * @brief Contains all data for a mission item.
 *
 * Each Mission object stores the title, description, associated image,
 * karma points, unique key, map type, and user ownership information.
 */
public class Mission {

    /** Image resource associated with the mission. */
    private int image;

    /** Title of the mission. */
    private String title;

    /** Karma points awarded for completing the mission. */
    private int karmaPoints;

    /** Description of the mission. */
    private String description;

    /** Unique key for identifying the mission. */
    private Long key;

    /** Type of map associated with this mission (if applicable). */
    private String mapType;

    /** Indicates whether the mission belongs to the current user. */
    private boolean user;

    /**
     * @brief Constructor to create a new Mission object.
     * @param title Title of the mission.
     * @param image Image resource ID for the mission.
     * @param karmaPoints Karma points awarded for the mission.
     * @param description Description of the mission.
     * @param key Unique key identifier for the mission.
     * @param mapType Type of map associated with this mission.
     * @param user Boolean indicating if this mission belongs to the user.
     */
    Mission(String title, int image, int karmaPoints, String description, Long key, String mapType, boolean user) {
        this.image = image;
        this.title = title;
        this.karmaPoints = karmaPoints;
        this.description = description;
        this.key = key;
        this.mapType = mapType;
        this.user = user;
    }

    /** @brief Returns the image resource ID. */
    public int getImage() { return image; }

    /** @brief Returns the mission title. */
    public String getTitle() { return title; }

    /** @brief Returns the karma points for the mission. */
    public int getKarmaPoints() { return karmaPoints; }

    /** @brief Returns the mission description. */
    public String getDescription() { return description; }

    /** @brief Returns the unique key for the mission. */
    public Long getKey() { return key; }

    /** @brief Returns the map type associated with the mission. */
    public String getMapType() { return mapType; }

    /** @brief Returns true if the mission belongs to the user. */
    public boolean getUser() { return user; }

    /**
     * @brief Compares this mission with another mission for equality.
     * @param other The other object to compare.
     * @return True if the keys are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) { return this.key.equals(((Mission) other).getKey()); }
}
