package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {


    static public ViewPager pager;
    static public int userChooser;
    static public SharedPreferencesHandler pref = new SharedPreferencesHandler();
    static public User user;
    static public String save;
    static public SharedPreferences.Editor editor;
    static public AlertDialog.Builder alertDialog;
    static public EditText sendmsg;
    private String mess;
    private String username;
    private int wait = 0;


    @Override
    public void onBackPressed() {

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Logout");
        final TextView input = new TextView(this);
        input.setTextSize(18);
        input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

        input.setText("Are you sure you want to logout?");

        alertDialog.setView(input);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String s = getIntent().getStringExtra("user");
        try {
            user = new User(s);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            System.exit(-1);
        }
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(user.getId(), MODE_PRIVATE);
        editor = preferences.edit();
        username = user.getUsername();
        /*
        new getTask().execute();
        while(wait == 0){}
        try {
            user.setMessages(mess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        //thread implemenation for message refresh when logged in...

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //user.
                                new getTask().execute();
                                while(wait == 0){}
                                try {
                                    user.setMessages(mess);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
    }



    private class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return FirstFragment.newInstance("View messages");
                case 1: return SecondFragment.newInstance("Contacts");
                case 2: return ThirdFragment.newInstance("Send messages");
                default: return ThirdFragment.newInstance("Send messages");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    class getTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("name", username));
            InputStream is = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/getMessages.php");
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
                mess = sb.toString();


            } catch (Exception e) {
                System.out.println("Error in getting messages: " + e.getMessage());

            }
            wait = 1;
            return null;
        }
    }

}