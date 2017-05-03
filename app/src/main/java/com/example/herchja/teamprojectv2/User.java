package com.example.herchja.teamprojectv2;

import android.os.AsyncTask;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;

/**
 * Created by akenf on 4/17/2017.
 */

public class User {
    private String username;
    private String id;
    private JSONObject data;
    ArrayList<Message> messages;
    ArrayList<String> contactList;
    private String publicKey;
    private PublicKey pubKey = null;

    /**
     * Constructor
     * @param username
     * @param id
     */
    public User(String username, String id){
        this.username = username;
        this.id = id;
    }

    /** Constructor
     *
     * @param s
     * @throws JSONException
     */
    public User(String s) throws JSONException {
        messages = new ArrayList<Message>();
        data = new JSONObject(s);
        this.username = data.getJSONArray("user").getJSONObject(0).getString("name");
        this.id = data.getJSONArray("user").getJSONObject(0).getString("id");

        // get the contact list for the user
        JSONArray raw = data.getJSONArray("user");
        JSONObject contacts = raw.getJSONObject(1);
        String cont = contacts.getString("contacts");
        contactList = new ArrayList<String>();
        if(cont.equals("") == false){
            contactList = new ArrayList<String>(Arrays.asList(cont.split(" ")));
        }
        contactList.add(0, "+ Add contact");

        // Create a task to get the public key from the database and wait for it to finish.
        myAsyncTask task = new myAsyncTask();
        try {
            Object result = task.execute().get();
        } catch (Exception e) {

        }
        // get the public key after execution is done.
        publicKey = task.onPostExecute();


        try { // convert the string key from the db over to a publicKey
            byte[] privateBytes = Base64.decodeBase64(publicKey.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            System.out.println("Error getting public key: " + e.getMessage());
        }

        // gets all the messages for the user
        for(int i = 2; i < raw.length(); i++){
            JSONObject mes = raw.getJSONObject(i);
            // Decode the message here! holy shit this is hard
            String encryptedText = mes.getString("text");
            String text = null;
            try { // decrypt the message for display
                text = decrypt(pubKey, encryptedText);
            } catch (Exception e) {
                System.out.println("Error decrypting key: " + e.getMessage());
            }

            // store the messages into an arraylist.
            Message temp = new Message(Integer.parseInt(mes.getString("id")), Integer.parseInt(this.id),  mes.getString("from"), text,
                    mes.getString("time"), mes.getString("salt"), mes.getString("timer"));
            messages.add(temp);
            System.out.println();
        }
        System.out.println();
    }

    /**
     *  Decrypt the message using a key from the database.
     * @param publicKey
     * @param encryptedText
     * @return
     * @throws Exception
     */
    public String decrypt(PublicKey publicKey, String encryptedText) throws Exception {
        byte[] bytes = Base64.decodeBase64(encryptedText.getBytes());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedText = cipher.doFinal(bytes);
        return new String(decryptedText);
    }

    /**
     * convert to string.
     * @return
     */
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

    // setters and getters for a bunch of stuff here...
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

    /**
     * Used to update the messages when refreshing.
     * @param s
     * @throws JSONException
     */
    public void setMessages(String s) throws JSONException {

        ArrayList<Message> tempA = new ArrayList<Message>();
        data = new JSONObject(s);
        JSONArray raw = data.getJSONArray("messages");
        for(int i = 0; i < raw.length(); i++){ // checking the length of the db messages
            JSONObject mes = raw.getJSONObject(i);
            String encryptedText = mes.getString("text");
            String text = null;
            try { // decrypt the text
                text = decrypt(pubKey, encryptedText);
            } catch (Exception e) {
                System.out.println("Error decrypting message: " + e.getMessage());
            }
            // store the text into an array list
            Message temp = new Message(Integer.parseInt(mes.getString("id")), Integer.parseInt(this.id),  mes.getString("from"), text,
                    mes.getString("time"), mes.getString("salt"), mes.getString("timer"));
            tempA.add(temp);
        }
        System.out.println();
        this.messages = tempA;
    }

    // more setters and getters
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

    /**
     * AsyncTask to run a HTTP call over to the server to run a php script, and get the
     * results of it. This is mainly to get the public key from the database.
     */
    class myAsyncTask extends AsyncTask<String, String, String> {
        public String publicKey = null;     // public key from database in string format.

        /**
         * Run this when you first create a new task.
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params){
            try {
                // get the private key from the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/readPub.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                // read the data from the php script that was executed.
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");
                is.close();
                String result = sb.toString();

                // get the message by decryption
                publicKey = result.replaceAll("publicKey", "");

            } catch (Exception e) {
                System.out.println("Error with getting message decryption:" + e.getMessage());
            }
            return publicKey;
        }

        // after the execution is done, this method will return the public key.
        protected String onPostExecute() {
            return publicKey;
        }
    }
}
