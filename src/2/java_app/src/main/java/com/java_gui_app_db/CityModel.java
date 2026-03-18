package com.java_gui_app_db;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CityModel {
    private final SimpleStringProperty city;
    private final SimpleDoubleProperty lat;
    private final SimpleDoubleProperty lng;
    private final SimpleStringProperty adminName;
    private final SimpleIntegerProperty population;

    public CityModel(String city, double lat, double lng, String adminName, int population) {
        this.city = new SimpleStringProperty(city);
        this.lat = new SimpleDoubleProperty(lat);
        this.lng = new SimpleDoubleProperty(lng);
        this.adminName = new SimpleStringProperty(adminName);
        this.population = new SimpleIntegerProperty(population);
    }

    public String getCity() { return city.get(); }
    public double getLat() { return lat.get(); }
    public double getLng() { return lng.get(); }
    public String getAdminName() { return adminName.get(); }
    public int getPopulation() { return population.get(); }
}