package masterIoT.mdp.karma.missions;

import android.net.Uri;

public class Mission {
    // This class contains the actual data of each item of the dataset

    private int image;
    private String title;
    private int karmaPoints;
    private String description;
    private Long key; // In this app we use keys of type Long
    private String mapType;
    private boolean user;

    Mission(String title, int image, int karmaPoints, String description, Long key, String mapType, boolean user) {
        this.image = image;
        this.title = title;
        this.karmaPoints = karmaPoints;
        this.description = description;
        this.key = key;
        this.mapType = mapType;
        this.user = user;
    }

    public int getImage() { return image; }
    public String getTitle() { return title; }
    public int getKarmaPoints() { return karmaPoints; }
    public String getDescription() { return description; }
    public Long getKey() { return key; }

    public String getMapType() { return mapType; }

    public boolean getUser() { return user; }


    @Override
    public boolean equals(Object other) { return this.key.equals(((Mission) other).getKey()); }

}