package com.cities_project;

public class Site {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String region;
    private String image;

    public Site(int id, String name, double latitude, double longitude, String region, String image) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
        this.image = image;
    }

    // Геттери необхідні для JavaFX TableView
    public int getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getRegion() { return region; }
    public String getImage() { return image; }
}