package com.example.herchja.teamprojectv2;

import java.util.ArrayList;

/**
 * Created by akenf on 4/17/2017.
 */

public class User {
    private String username;
    private int id;
    ArrayList<String> messages;

    public User(String username, int id) {
        this.username = username;
        this.id = id;
        //messages = getMessages(id);
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
