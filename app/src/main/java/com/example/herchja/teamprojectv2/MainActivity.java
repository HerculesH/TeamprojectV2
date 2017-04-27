package com.example.herchja.teamprojectv2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;

import com.example.herchja.teamprojectv2.R;

public class MainActivity extends FragmentActivity {

    static public ViewPager pager;
    static public ArrayList<User> listItems = new ArrayList<User>();
    static public ArrayList<String> msgItems = new ArrayList<String>();
    static public ArrayList<String> groupMsg = new ArrayList<>();
    static public DatabaseHandler db = new DatabaseHandler("","semaster","3ab7jz24s");
    static public User eUser;
    static public int userChooser;
    static public SharedPreferences mPrefs;
    static public SharedPreferencesHandler pref = new SharedPreferencesHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);

        if(listItems.isEmpty()) {

            eUser = new User("+ Add contact","");
            listItems.add(eUser);

        }
        else
        {
            listItems = pref.getArray(mPrefs);
        }

        if(msgItems.isEmpty()) {
            msgItems.add("new message");
        }

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


}