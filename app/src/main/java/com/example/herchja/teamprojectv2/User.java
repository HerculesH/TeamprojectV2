package com.example.herchja.teamprojectv2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by akenf on 4/17/2017.
 */

public class User  {
    private String username;
    private String id;
    private JSONObject data;
    ArrayList<Message> messages;
    ArrayList<String> contactList;

    public User(String username, String id){
        this.username = username;
        this.id = id;
    }
    public User(String s) throws JSONException {
        messages = new ArrayList<Message>();
        data = new JSONObject(s);
        this.username = data.getJSONArray("user").getJSONObject(0).getString("name");
        this.id = data.getJSONArray("user").getJSONObject(0).getString("id");

        JSONArray raw = data.getJSONArray("user");
        JSONObject contacts = raw.getJSONObject(1);
        String cont = contacts.getString("contacts");
        contactList = new ArrayList<String>();
        if(cont.equals("") == false){
            contactList = new ArrayList<String>(Arrays.asList(cont.split(" ")));
        }
        contactList.add(0, "+ Add contact");
        for(int i = 2; i < raw.length(); i++){
            JSONObject mes = raw.getJSONObject(i);
            Message temp = new Message(Integer.parseInt(mes.getString("id")), Integer.parseInt(this.id),  mes.getString("from"), mes.getString("text"),
                    mes.getString("time"), mes.getString("salt"), mes.getString("timer"));
            messages.add(temp);
            System.out.println();
        }
        System.out.println();
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

    public ArrayList<String> getContactList() {
        return contactList;
    }

    public void remContact(int ndx) {
        contactList.remove(ndx);
    }

    public void setMessages(String s) throws JSONException {

        //this.messages = messages;
        ArrayList<Message> tempA = new ArrayList<Message>();
        data = new JSONObject(s);
        JSONArray raw = data.getJSONArray("messages");
        for(int i = 0; i < raw.length(); i++){
            JSONObject mes = raw.getJSONObject(i);
            Message temp = new Message(Integer.parseInt(mes.getString("id")), Integer.parseInt(this.id),  mes.getString("from"), mes.getString("text"),
                    mes.getString("time"), mes.getString("salt"), mes.getString("timer"));
            tempA.add(temp);

        }
        System.out.println();
        this.messages = tempA;
    }

    public void setContact(String name, int ndx) {
        contactList.set(ndx, name);
    }

    public void setId(String id) {

        this.id = id;
    }

    public void remMessage(int ndx) {

        messages.remove(ndx);
    }

    public ArrayList<Message> getMessages() {

        return messages;
    }

    public void delMessages(int ndx) {
        messages.remove(ndx);
    }

}
