package com.example.herchja.teamprojectv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.RandomAccessFile;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.apache.commons.codec.binary.Base64;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    private EditText Name, Username, Password, Password2;
    private byte[] salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, new HashMap());
        task.execute("http://54.148.185.237/readSalt.php");

    }

    @Override
    public void processFinish(String s) {
        if (s.contains("salt")) {
            Pattern p = Pattern.compile("salt\n");
            Matcher m = p.matcher(s);
            s.replaceAll("salt\n", "");
            salt = s.getBytes();
        }
        else if (s.equals("registered")) {
            //do something after register
            Toast.makeText(this, "Register successful", Toast.LENGTH_LONG).show();
        } else if (s.contains("Login Failed") == false) {
            if (s.contains("User is blocked") == false) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", s);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User is blocked!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed Login Attempt", Toast.LENGTH_LONG).show();
        }
    }

    public void login(View view)
    {
        Username = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        View Error = findViewById(R.id.LoginError);
        byte[] salt = getSalt();
        byte[] hash = hash(Password.getText().toString().toCharArray(), salt);
        byte[] encode = Base64.encodeBase64(hash);

        HashMap postData = new HashMap();
        postData.put("mobile", "android");
        postData.put("txtUsername", Username.getText().toString());
        postData.put("txtPassword", new String(encode));

        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://54.148.185.237/login.php");

    }
    public void register(View view){
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

        if(n.length() > 1){
            if(p.equals(p2)){
                registerUser(n, u, p, "0");
                swapToLogin(view);
            }
            else{
                Toast.makeText(this, "Passwords don't Match.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Name not long enough.", Toast.LENGTH_LONG).show();
        }

    }

    public void registerUser(String name, String username, String pass, String valid) {


        HashMap postData = new HashMap();
        byte[] salt = getSalt();
        byte[] passHash = hash(pass.toCharArray(), salt);
        byte[] encoded = Base64.encodeBase64(passHash);

        // String storeHash = "0x" + BitConverter.ToString(passHash).Replace("-", "");

        postData.put("name", name);
        postData.put("username", username);
        postData.put("password", new String(encoded));
        postData.put("valid", valid);
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://54.148.185.237/registerUser.php");

    }
    public byte[] getSalt() {
        return salt;
    }

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
