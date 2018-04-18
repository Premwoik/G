package com.example.geoxplore.api.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by prw on 18.04.18.
 */

public class HomeCords {

    private String longitude;
    private String latitude;

    public HomeCords(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public HomeCords(LatLng l) {
        this.latitude = String.valueOf(l.getLatitude());
        this.longitude = String.valueOf(l.getLongitude());
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
