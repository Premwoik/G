package com.example.geoxplore.api.model;

/**
 * Created by prw on 09.06.18.
 */

public class AddFriendResponse {
    String message;

    public AddFriendResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
