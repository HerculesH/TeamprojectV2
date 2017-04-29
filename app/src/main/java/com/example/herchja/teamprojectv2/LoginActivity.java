package com.example.herchja.teamprojectv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    private EditText Username, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void processFinish(String s) {
        if(s.contains("Login Failed") == false){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", s);
            startActivity(intent);
        }
        else {
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
}
