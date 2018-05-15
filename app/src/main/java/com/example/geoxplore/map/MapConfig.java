package com.example.geoxplore.map;

/**
 * Created by prw on 15.05.18.
 */

public interface MapConfig {
    int maxZoom = 19;
    int minZoom = 13;
    int defaultZoom = 16;
    double range = 0.001; //TODO okreslic jakas sensowna odległość od skrzynki
}
