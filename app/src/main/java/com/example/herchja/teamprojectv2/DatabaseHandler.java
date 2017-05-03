package com.example.herchja.teamprojectv2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/**
 * Created by Danny Nguyen on 4/5/2017.
 * just ignore this file for now lol.
 */

public class DatabaseHandler {

    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * Constructor
     */
    public DatabaseHandler() {

    }


    /**
     * getNextSalt will create a random salt
     * ****** Danny's Notes *******
     * We need to store this somewhere, and use this same salt for passwords. If we don't, we can't check
     * directly with the database if the password is correct.
     * @return a 16-byte randomly generated salt
     */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Create a hash off of the password and randomly generated salt.
     *
     * @param password  User password input.
     * @param salt      Salt generated from getNextSalt()
     * @return          Hash value of password input and the salt.
     */
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new Error("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }



    /**
     * This will send a message to the database for the users to read.
     * @param idTo      The id the user will be sending to
     * @param idFrom    The id of the current using (who it's from)
     * @param message   The message itself.
     */
    public static void sendMessage(String idTo, String idFrom, String message, String salt) {

        ArrayList<Message> msg = new ArrayList<Message>();
        ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("toid", idTo));
        nvp.add(new BasicNameValuePair("fromid", idFrom));
        nvp.add(new BasicNameValuePair("text", message));
        nvp.add(new BasicNameValuePair("salt", salt));
        InputStream is = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://54.148.185.237/sendMessages.php");
            httppost.setEntity(new UrlEncodedFormEntity(nvp));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            System.out.println("Error in getting messages: " + e.getMessage());

        }

    }

    public static boolean registerUser(String name, String username, String pass, String valid) {

        try {
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("name", name));
            nvp.add(new BasicNameValuePair("username", username));
            nvp.add(new BasicNameValuePair("password", pass));
            nvp.add(new BasicNameValuePair("valid", valid));
            InputStream is = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://54.148.185.237/registerUser.php");
            httppost.setEntity(new UrlEncodedFormEntity(nvp));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");

            is.close();
            String result = sb.toString();
            if (result.contains(" ")) {
                return false;
            }


        } catch (Exception e) {
            System.out.println("Error in getting messages: " + e.getMessage());
            return false;

        }
        return true;

    }

    /**
     * This will check a password with its expected hash, and return true or false if the hash and the
     * input password matches.
     *
     * @param password          User password that is typed in
     * @param salt              Salt that we have stored somewhere
     * @param expectedHash      Hash that we are expecting, we retrieve this from the database.
     * @return                  True if password hash matches the database one, false otherwise.
     */
    public static boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) return false;
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }
    
    /**
     * Takes username and password from input and checks if the user exist and if the 
     * password matches and returnsa boolean.
     *
    public boolean Login(String userID, char[] password){
    	 String query = "SELECT usernamr FROM Users WHERE userid=" + userID;
    	 byte[] salt = getNextSalt();
    	 byte[] hashPass = getPassword(userID);
         try {
             Statement stmt = this.connection.createStatement(); // connect to database
             stmt.executeQuery(query);                           // execute query
             ResultSet rs = stmt.getResultSet();                 // get results
             if(!rs.isBeforeFirst()){                            // checks if user is registered
            	 //System.out.println("User not found");
            	 stmt.close();                                       
                 rs.close();
            	 return false;
             }
             stmt.close();                                       // close connections
             rs.close();
             if(isExpectedPassword(password, salt, hashPass))    //check if correct password
            	 return true;
             else {
            	//System.out.println("Incorrect password");
            	 return false;                                   //rutuns false if incorrect password
             }
         } catch (Exception e) {
             System.out.println("Error executing statement!");
             return false;
         }
    	
    }*/
}
