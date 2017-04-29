package com.example.herchja.teamprojectv2;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/**
 * Created by Danny Nguyen on 4/5/2017.
 */

public class DatabaseHandler {

    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private Connection connection;


    /**
     * Domain name for our project:
     *      jdbc:mysql://" + "se-team4-project.cyl2fljhshrg.us-west-2.rds.amazonaws.com:3306/se4_mydb
     *
     * @param domain        domain name. Hardcoded this into the code.
     * @param username      username. Only private users should know this
     * @param password      password. Only private users should know this.
     */
    public DatabaseHandler(String domain, String username, String password) {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + "se-team4-project.cyl2fljhshrg.us-west-2.rds.amazonaws.com:3306/se4_mydb",
                    username, password);
        } catch (Exception e) { System.out.println("Connection Failed!:\n" + e.getMessage()); }

    }


    /**
     * this will close the connection of the database handler.
     */
    public void close() {
        try {
            this.connection.close();
        } catch (Exception e) { System.out.println("Unable to close connection"); }
    }


    /**
     * This will get all of the usernames within the database and return an arraylist of all of them.
     * @return  Arraylist of usernames in the database.
     */
    public ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();
        String query = "Select username from Users"; // select all users from username
        try {
            Statement stmt = this.connection.createStatement(); // connect to database
            stmt.executeQuery(query);                           // execute query
            ResultSet rs = stmt.getResultSet();                 // get results
            while (rs.next()) {  // while there are things to read
                users.add(rs.getString("username")); // add to arraylist
            }
            stmt.close();   // close connections
            rs.close();
        } catch (Exception e) {
            System.out.println("Error executing statement!");
        }
        return users;
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
     *  This will get the password stored in the database.
     * @param userID    username to check
     * @return          hashed password stored in database
     */
    public byte[] getPassword(String userID) {
        byte[] pass = new byte[30];
        String query = "SELECT password FROM Users WHERE userid=" + userID; // select all users from username
        try {
            Statement stmt = this.connection.createStatement(); // connect to database
            stmt.executeQuery(query);                           // execute query
            ResultSet rs = stmt.getResultSet();                 // get results
            pass = rs.getBytes("password");
            stmt.close();   // close connections
            rs.close();
        } catch (Exception e) {
            System.out.println("Error executing statement!");
        }
        return pass;
    }


    /**
     * This will send a message to the database for the users to read.
     * @param idTo      The id the user will be sending to
     * @param idFrom    The id of the current using (who it's from)
     * @param message   The message itself.
     */
    public void sendMessage(int idTo, int idFrom, String message) {

        ArrayList<Message> msg = new ArrayList<Message>();
        ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("toid", Integer.toString(idTo)));
        nvp.add(new BasicNameValuePair("fromid", Integer.toString(idFrom)));
        nvp.add(new BasicNameValuePair("text", message));
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

    /**
     *  This will register a user into the database.
     * @param username      Username
     * @param hashPass      Password (After it has been hashed)
     * @param valid         Valid bit, always assume its 0 first.
     * @return              True if there are no errors.
     */
    public boolean registerUser(String username, byte[] hashPass, int valid) {
        String query = "INSERT INTO Users(username, password, valid) VALUES (" +
                username + "," + hashPass + "," + valid + ")";
        if (executeInsert(query)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function will execute any inserting queries for the database.
     * @param query     The query to execute
     * @return          True if it works, false if there's an error.
     */
    private boolean executeInsert(String query) {
        try {
            Statement stmt = this.connection.createStatement();
            stmt.executeQuery(query);
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error executing Statement: " + query + "\nError: " + e.getMessage());
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
     */
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
    	
    }
}
