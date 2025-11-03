package masterIoT.mdp.karma.missions;

public class Mission {
    // This class contains the actual data of each item of the dataset

    private int image;
    private String title;
    private int karmaPoints;
    private String description;
    private Long key; // In this app we use keys of type Long

    Mission(String title, int image, int karmaPoints, String description, Long key) {
        this.image = image;
        this.title = title;
        this.karmaPoints = karmaPoints;
        this.description = description;
        this.key = key;
    }

    public int getImage() { return image; }

    public String getTitle() {
        return title;
    }

    public int getKarmaPoints() {
        return karmaPoints;
    }

    public String getDescription() {
        return description;
    }

    public Long getKey() {
        return key;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Items):
    public boolean equals(Object other) {
        return this.key == ((Mission) other).getKey();
    }

}