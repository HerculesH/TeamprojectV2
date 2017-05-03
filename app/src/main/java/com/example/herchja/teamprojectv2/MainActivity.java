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

/**
 * This handles the main activity of the application
 */
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

    /**
     * This handles when the back button is pressed on the phone.
     */
    @Override
    public void onBackPressed() {
        // initialize interface stuff
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Logout");
        final TextView input = new TextView(this);
        input.setTextSize(18);
        input.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        input.setText("Are you sure you want to logout?");

        alertDialog.setView(input);
        alertDialog.setCancelable(true);
        // when the user clicks yes when they wanna exit
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        // when the user clicks no when they wanna exit
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    /**
     * This will execute on creation after the user logs into their account.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s = getIntent().getStringExtra("user");

        try { // create a new user with the information gained from the login activity.
            user = new User(s);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            System.exit(-1);
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(user.getId(), MODE_PRIVATE);
        editor = preferences.edit();
        username = user.getUsername();
        Thread t = new Thread() { // thread creator for auto-refreshing messages
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

        // change the page that is currently being viewed.
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
    }

    /**
     * Class for page adaptation.
     */
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        /**
         * Handles which fragment to be displaying
         * @param pos
         * @return
         */
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

    /**
     * Task to run in the background when getting messages and updating messages for the user.
     */
    class getTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("name", username));
            InputStream is = null;

            try { // handles connection with the server to execute a script to get the messages.
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