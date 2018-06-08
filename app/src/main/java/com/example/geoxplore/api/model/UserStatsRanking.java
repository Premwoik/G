package com.example.geoxplore.api.model;

/**
 * Created by prw on 18.04.18.
 */

public class UserStatsRanking {
    String username;
    int level;
    int openedChests;
    int lastWeekChests;


    public UserStatsRanking(String username, int level, int openedChests, int lastWeekChests) {
        this.username = username;
        this.level = level;
        this.openedChests = openedChests;
        this.lastWeekChests = lastWeekChests;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public int getOpenedChests() {
        return openedChests;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setOpenedChests(int openedChests) {
        this.openedChests = openedChests;
    }

    public int getLastWeekChests() {
        return lastWeekChests;
    }

    public void setLastWeekChests(int lastWeekChests) {
        this.lastWeekChests = lastWeekChests;
    }
}
