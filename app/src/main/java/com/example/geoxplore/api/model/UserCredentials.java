package com.example.geoxplore.api.model;

/**
 * Created by Kayser Sose on 2018-04-17.
 */

/**
 * Created by prw on 16.04.18.
 */

public class UserCredentials {
    private String username;
    private String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static boolean validate(UserCredentials credentials){
        return !credentials.getPassword().isEmpty() && !credentials.getUsername().isEmpty();
    }
}