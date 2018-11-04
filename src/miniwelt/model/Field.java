package miniwelt.model;

public enum Field {
    EMPTY(""),
    WALL("W"),
    DANGER("D"),
    KARL("K"),
    KARL_AT_OFFICE("KO"),
    KARL_ON_DANGER("KD"),
    OFFICE("O");

    private String key;

    Field(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
