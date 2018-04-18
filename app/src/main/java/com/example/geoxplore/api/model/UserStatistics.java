package com.example.geoxplore.api.model;

/**
 * Created by prw on 18.04.18.
 */

public class UserStatistics {
    private String username;
    private int experience;
    private int level;
    private double toNextLevel;
    private int openedChests;

    public UserStatistics(String username, int experience, int level, double toNextLevel, int openedChests) {
        this.username = username;
        this.experience = experience;
        this.level = level;
        this.toNextLevel = toNextLevel;
        this.openedChests = openedChests;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getToNextLevel() {
        return toNextLevel;
    }

    public void setToNextLevel(double toNextLevel) {
        this.toNextLevel = toNextLevel;
    }

    public int getOpenedChests() {
        return openedChests;
    }

    public void setOpenedChests(int openedChests) {
        this.openedChests = openedChests;
    }
}
