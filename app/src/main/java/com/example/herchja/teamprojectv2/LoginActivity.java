package com.example.herchja.teamprojectv2;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public boolean checkpassword(String PW)
    {
        if(PW.contentEquals("0"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void login(View view)
    {
        EditText Account = (EditText) findViewById(R.id.editText);
        String ID = Account.getText().toString();

        EditText Password = (EditText) findViewById(R.id.editText2);
        String input = Password.getText().toString();

        View Error = findViewById(R.id.LoginError);


        //
        //Input the login code here
        //

        if (ID.contentEquals("1234") && checkpassword(input))
        {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (ID.contentEquals("Admin") && checkpassword(input))
        {
            Intent intent = new Intent(this, Admin.class);
            startActivity(intent);
        }
        else
        {
            Error.setVisibility(View.VISIBLE);
        }

    }
}
