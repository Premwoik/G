package com.example.geoxplore.api.model;

/**
 * Created by prw on 18.04.18.
 */

public class UserStatistics {
    private String username;
    private int experience;
    private int level;
    private double toNextLevel;
    private int friends;
    private ChestStats chestStats;

    public UserStatistics(String username, int experience, int level, double toNextLevel, int friends, ChestStats chestStats) {
        this.username = username;
        this.experience = experience;
        this.level = level;
        this.toNextLevel = toNextLevel;
        this.friends = friends;
        this.chestStats = chestStats;
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

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public ChestStats getChestStats() {
        return chestStats;
    }

    public void setChestStats(ChestStats chestStats) {
        this.chestStats = chestStats;
    }
}
