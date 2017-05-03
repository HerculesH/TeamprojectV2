package com.example.herchja.teamprojectv2;

/**
 * Created by akenf on 4/17/2017.
 */

/**
 * Creates the message object being used to send between users
 */
public class Message {
    private int id;
    private int toid;
    private String from;
    private String timestamp;
    private String text;
    private int timer;
    private String salt;

    /**
     * Message constructor
     * @param id id of the message
     * @param toid to which it will be sent
     * @param from to which the message is from
     * @param text the text of the message
     * @param timestamp the timerstamp it was created
     * @param salt the encryption of the message
     * @param timer the timer which is selected by the user
     */

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
