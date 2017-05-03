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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
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

    public User(String username, String id){
        this.username = username;
        this.id = id;
    }


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

        // gets all the messages for the user
        for(int i = 2; i < raw.length(); i++){
            JSONObject mes = raw.getJSONObject(i);
            // Decode the message here! holy shit this is hard
            String encryptedText = mes.getString("text");
            String text = null;
            try {
                // get the private key from the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/readPub.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                is.close();
                String result = sb.toString();
                String publicKey = result.replaceAll("publicKey", "");
                byte[] privateBytes = Base64.decodeBase64(publicKey.getBytes());
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(privateBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey pubKey = keyFactory.generatePublic(keySpec);
                text = decrypt(pubKey, encryptedText);

            } catch (Exception e) {
                System.out.println("Error with http responses: " + e.getMessage());

            }
            Message temp = new Message(Integer.parseInt(mes.getString("id")), Integer.parseInt(this.id),  mes.getString("from"), encryptedText,
                    mes.getString("time"), mes.getString("salt"), mes.getString("timer"));
            messages.add(temp);
            System.out.println();
        }
        System.out.println();
    }
    public String decrypt(PublicKey publicKey, String encryptedText) throws Exception {
        byte[] bytes = Base64.decodeBase64(encryptedText.getBytes());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return new String(cipher.doFinal(bytes));
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

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void setContact(String name, int ndx) {
        contactList.set(ndx, name);
    }

    public void setUsername(String name) {
        this.username = name;
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

    class myAsyncTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params){

            try {
                // get the private key from the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/readPub.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                is.close();
                String result = sb.toString();
                String publicKey = result.replaceAll("publicKey", "");
                byte[] privateBytes = Base64.decodeBase64(publicKey.getBytes());
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(privateBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey pubKey = keyFactory.generatePublic(keySpec);



            } catch (Exception e) {

            }


                return null;
        }

        /**
         *  Decrypt the message given a public key.
         * @param publicKey
         * @param encrypted
         * @return
         * @throws Exception
         */
        public String decrypt(PublicKey publicKey, byte [] encrypted) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(encrypted));
        }
    }

}
