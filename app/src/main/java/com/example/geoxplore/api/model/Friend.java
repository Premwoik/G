package com.example.geoxplore.api.model;

/**
 * Created by prw on 09.06.18.
 */

public class Friend {
    String username;
    int level;
    int openedChests;

    public Friend(String username, int level, int openedChests) {
        this.username = username;
        this.level = level;
        this.openedChests = openedChests;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getOpenedChests() {
        return openedChests;
    }

    public void setOpenedChests(int openedChests) {
        this.openedChests = openedChests;
    }
}
