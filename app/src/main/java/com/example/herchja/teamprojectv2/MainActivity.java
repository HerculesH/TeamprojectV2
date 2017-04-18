package com.example.herchja.teamprojectv2;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.herchja.teamprojectv2.R;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
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