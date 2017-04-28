package com.example.herchja.teamprojectv2;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.*;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.*;
import java.security.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    private EditText Username, Password;
    DatabaseHandler db = DatabaseHandler.getInstance();
    //ArrayList<Message> mes = db.getMessages(1141);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void processFinish(String s) {
        if(s.equals("Login Failed") == false){
            Intent intent = new Intent(this, MainActivity.class);
            String re = "\\D+(\\d+).*name\\W+([A-Za-z]+)";
            Pattern p = Pattern.compile(re);
            String id, name;
            Matcher m = p.matcher(s);

            if(m.find()){
                id = m.group(1);
                name = m.group(2);
                intent.putExtra("User", name);
                intent.putExtra("ID", id);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Process Error", Toast.LENGTH_LONG).show();
            }
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
