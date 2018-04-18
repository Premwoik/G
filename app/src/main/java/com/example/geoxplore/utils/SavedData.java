package com.example.geoxplore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by prw on 18.04.18.
 */

public final class SavedData {
    private static final String USR_HOME_LATITUDE = "usr_home_latitude";
    private static final String USR_HOME_LONGITUDE = "usr_home_longitude";

    private static SharedPreferences getPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserHome(Context ctx, LatLng point){
        SharedPreferences.Editor editor = getPreferences(ctx).edit();
        editor.putLong(USR_HOME_LATITUDE, Double.doubleToLongBits(point.getLatitude()));
        editor.putLong(USR_HOME_LONGITUDE, Double.doubleToLongBits(point.getLongitude()));
        editor.apply();
    }

    public static LatLng getUserHome(Context ctx){
        long none = Double.doubleToLongBits(0);
        double longitude =Double.longBitsToDouble(getPreferences(ctx).getLong(USR_HOME_LONGITUDE, none));
        double latitude = Double.longBitsToDouble(getPreferences(ctx).getLong(USR_HOME_LATITUDE, none));

        return longitude !=0 && latitude !=0? new LatLng(latitude,longitude): null;
    }

    public static void clear(Context ctx){
        SharedPreferences.Editor editor = getPreferences(ctx).edit();
        editor.remove(USR_HOME_LONGITUDE);
        editor.remove(USR_HOME_LATITUDE);
        editor.apply();
    }
}
