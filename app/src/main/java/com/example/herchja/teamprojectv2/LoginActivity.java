package com.example.herchja.teamprojectv2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.apache.commons.codec.binary.Base64;

import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This will handle activity that is going on during the login process.
 */
public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    private EditText Name, Username, Password, Password2;
    private byte[] salt;

    public static void restart(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(0);
    }

    /**
     * Execute this function when this is first created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // read the salt from the server and save it into the salt variable.
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, new HashMap());
        task.execute("http://54.148.185.237/readSalt.php");

    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    /**
     * This is executed after a task is finish executing. This will read the output of the
     * task, and do certain actions depending on the output.
     * @param s     output from php script.
     */
    @Override
    public void processFinish(String s) {
        if (s.contains("salt") && salt == null) { // if the php script is handling the readsalt
            s = s.replaceAll("salt", "");
            salt = s.getBytes();
        } else if (s.equals("registered")) { // handles the registration of a new user
            //do something after register
            Toast.makeText(this, "Register successful", Toast.LENGTH_LONG).show();
        } else if (s.equals("exists") || s.equals("Unable to insert values.")) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show();

        } else if (s.contains("Login Failed") == false) {
            if (s.contains("User is blocked") == false) { // if the user passes login, add the intent and start another activity
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", s);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User is blocked!", Toast.LENGTH_LONG).show();
            }
        } else { // failed logins
            Toast.makeText(this, "Failed Login Attempt", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the login functions (Username/Password checking) - Hashes the password before checking
     * with the PHP script.
     * @param view
     */
    public void login(View view)
    {

        // get the information
        Username = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        View Error = findViewById(R.id.LoginError);


        if(Username.getText().toString().isEmpty() || Password.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill in empty fields", Toast.LENGTH_LONG).show();
        }
        // store the data into a hashmap for the phpscript to read
        else {
            byte[] salt = getSalt();
            byte[] hash = hash(Password.getText().toString().toCharArray(), salt);
            byte[] encode = Base64.encodeBase64(hash);
            HashMap postData = new HashMap();
            postData.put("mobile", "android");
            postData.put("txtUsername", Username.getText().toString());
            postData.put("txtPassword", new String(encode));

            // execute the Asynchroized task with data from the hashmap.
            PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
            task.execute("http://54.148.185.237/login.php");
        }

    }

    /**
     * Handles the registration form when creating a new user.
     * @param view
     */
    public void register(View view){
        // Get the different fields from the registration form
        Name = (EditText) findViewById(R.id.editText9);
        Username = (EditText) findViewById(R.id.editText4);
        Password = (EditText) findViewById(R.id.editText7);
        Password2 = (EditText) findViewById(R.id.editText8);
        System.out.println();
        String n, u, p, p2;
        n = Name.getText().toString();
        u = Username.getText().toString();
        p = Password.getText().toString();
        p2 = Password2.getText().toString();

        // add in parameter checking (ex. length pass, etc)
        if(n.length() > 1){
            if(p.equals(p2)){ // if password passes the check, register the user and switch views
                registerUser(n, u, p, "0");
                swapToLogin(view);
            }
            // else Print out error messages
            else{
                Toast.makeText(this, "Passwords don't Match.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Name not long enough.", Toast.LENGTH_LONG).show();
        }

    }


    /**
     *  Registers the user after the fields have been filled out
     * @param name          Name of the user
     * @param username      Username of the user
     * @param pass          Password of the user
     * @param valid         Valid bit of the user (defaulted at 0)
     */
    public void registerUser(String name, String username, String pass, String valid) {
        // generate the hash and encode the password over to base64 for php script reading
        byte[] salt = getSalt();
        byte[] passHash = hash(pass.toCharArray(), salt);
        byte[] encoded = Base64.encodeBase64(passHash);

        // store the data into a hashmap for the phpscript to read
        HashMap postData = new HashMap();
        postData.put("name", name);
        postData.put("username", username);
        postData.put("password", new String(encoded));
        postData.put("valid", valid);

        // execute the async task
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://54.148.185.237/registerUser.php");

    }

    /**
     * Get the salt that is stored in the file. The salt is saved on creation.
     * @return
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * This will hash the password with the salt stored on the server. This will use
     * PBKDF2 (Password-based Key derivative Function 2) algorithm for encryption.
     * @param password  password to hash
     * @param salt      salt for the first PBE encryption.
     * @return          byte[] of the hashed values.
     */
    public byte[] hash(char[] password, byte[] salt) {
        int ITERATIONS = 1000;
        int KEY_LENGTH = 256;
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

    public void swapToRegister(View view){
        setContentView(R.layout.fragment_register_user);
    }
    public void swapToLogin(View view){
        setContentView(R.layout.activity_login);
    }
}
