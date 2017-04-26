package com.example.herchja.teamprojectv2;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HercHja on 4/26/17.
 */

public class SharedPreferencesHandler {

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;

    public void saveArray(ArrayList<String> list, Activity a)
    {
        sharedPref = a.getPreferences(a.MODE_PRIVATE);
        editor = a.getPreferences(a.MODE_PRIVATE).edit();

        editor.putString("yourKey", list.toString());
        editor.commit();
    }


}
