package com.example.herchja.teamprojectv2;

import com.kosalgeek.asynctask.AsyncResponse;

import java.util.ArrayList;

/**
 * Created by akenf on 4/17/2017.
 */

public class User implements AsyncResponse {
    private String username;
    private String id;
    ArrayList<String> messages;

    public User(String username, String id) {
        this.username = username;
        this.id = id;
        //messages = getMessages(id);
    }

    public String toString()
    {
        if(this.getId() != "") {
            return this.getUsername() + " <ID: " + this.getId() + ">";
        }
        else
        {
            return this.getUsername();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setId(String id) {

        this.id = id;
    }

    public ArrayList<String> getMessages() {

        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    @Override
    public void processFinish(String s) {

    }
}
