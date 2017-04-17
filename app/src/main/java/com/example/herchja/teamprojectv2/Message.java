package com.example.herchja.teamprojectv2;

/**
 * Created by akenf on 4/17/2017.
 */

public class Message {
    private int UserId;
    private int SendId;
    private String text;
    private String state;

    public Message(int userId, int sendId, String text, String state) {
        UserId = userId;
        SendId = sendId;
        this.text = text;
        this.state = state;
    }

    public int getUserId() {
        return UserId;
    }

    public int getSendId() {
        return SendId;
    }

    public String getMessage() {
        return text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
