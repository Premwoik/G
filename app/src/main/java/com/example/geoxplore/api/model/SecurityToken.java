package com.example.geoxplore.api.model;

/**
 * Created by prw on 16.04.18.
 */

public class SecurityToken {
    private String token;

    public SecurityToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}