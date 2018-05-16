package com.example.geoxplore.api.model;

import android.accessibilityservice.GestureDescription;

/**
 * Created by prw on 16.05.18.
 */

public class OpenBoxResponseData {
    int expGained;

    public OpenBoxResponseData(int expGained) {
        this.expGained = expGained;
    }

    public int getExpGained() {
        return expGained;
    }

    public void setExpGained(int expGained) {
        this.expGained = expGained;
    }

    public boolean isValid(){
        return expGained != -1;
    }
}
