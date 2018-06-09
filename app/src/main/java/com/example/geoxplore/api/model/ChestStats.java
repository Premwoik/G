package com.example.geoxplore.api.model;

/**
 * Created by prw on 08.06.18.
 */

public class ChestStats {
    private int openedOverallCommonChests;
    private int openedOverallRareChests;
    private int openedOverallEpicChests;
    private int openedOverallLegendaryChests;
    private int openedLastWeekChests;

    public ChestStats(int openedOverallCommonChests, int openedOverallRareChests, int openedOverallEpicChests, int openedOverallLegendaryChests, int openedLastWeekChests) {
        this.openedOverallCommonChests = openedOverallCommonChests;
        this.openedOverallRareChests = openedOverallRareChests;
        this.openedOverallEpicChests = openedOverallEpicChests;
        this.openedOverallLegendaryChests = openedOverallLegendaryChests;
        this.openedLastWeekChests = openedLastWeekChests;
    }


    public int getOpenedOverallCommonChests() {
        return openedOverallCommonChests;
    }

    public void setOpenedOverallCommonChests(int openedOverallCommonChests) {
        this.openedOverallCommonChests = openedOverallCommonChests;
    }

    public int getOpenedOverallRareChests() {
        return openedOverallRareChests;
    }

    public void setOpenedOverallRareChests(int openedOverallRareChests) {
        this.openedOverallRareChests = openedOverallRareChests;
    }

    public int getOpenedOverallEpicChests() {
        return openedOverallEpicChests;
    }

    public void setOpenedOverallEpicChests(int openedOverallEpicChests) {
        this.openedOverallEpicChests = openedOverallEpicChests;
    }

    public int getOpenedOverallLegendaryChests() {
        return openedOverallLegendaryChests;
    }

    public void setOpenedOverallLegendaryChests(int openedOverallLegendaryChests) {
        this.openedOverallLegendaryChests = openedOverallLegendaryChests;
    }

    public int getOpenedLastWeekChests() {
        return openedLastWeekChests;
    }

    public void setOpenedLastWeekChests(int openedLastWeekChests) {
        this.openedLastWeekChests = openedLastWeekChests;
    }
}
