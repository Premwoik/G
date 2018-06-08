package com.example.geoxplore.api.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by prw on 18.04.18.
 */

public class Chest {
    private long id;
    private double longitude;
    private double latitude;
    private boolean opened;
    private long value;

    public Chest(double longitude, double latitude, boolean opened, long value) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.opened = opened;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public LatLng getLang() {
        return new LatLng(latitude, longitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public long getValue() {return value;}

    public void setValue(long value) {this.value = value;}
}
