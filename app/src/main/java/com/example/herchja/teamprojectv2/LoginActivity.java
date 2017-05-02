package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    private EditText Name, Username, Password, Password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void processFinish(String s) {
        if (s.equals("registered")) {
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

        HashMap postData = new HashMap();
        postData.put("mobile", "android");
        postData.put("txtUsername", Username.getText().toString());
        postData.put("txtPassword", Password.getText().toString());

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
        postData.put("name", name);
        postData.put("username", username);
        postData.put("password", pass);
        postData.put("valid", valid);
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://54.148.185.237/registerUser.php");

    }

    public void swapToRegister(View view){
        setContentView(R.layout.fragment_register_user);
    }
    public void swapToLogin(View view){
        setContentView(R.layout.activity_login);
    }
}
