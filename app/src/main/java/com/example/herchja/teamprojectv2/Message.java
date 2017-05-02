package com.example.herchja.teamprojectv2;

/**
 * Created by akenf on 4/17/2017.
 */

public class Message {
    private int id;
    private int toid;
    private String from;
    private String timestamp;
    private String text;
    private int timer;
    private String salt;



    public Message(int id, int toid, String from, String text, String timestamp, String salt, String timer) {
        this.id = id;
        this.toid = toid;
        this.from = from;
        this.timestamp = timestamp;

        this.text = text;
        this.salt = salt;
        this.timer = Integer.parseInt(timer);
    }

    public int getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public int getToid() {
        return toid;
    }

    public String getFrom() {
        return from;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public int getTimer() {
        return timer;
    }
}
