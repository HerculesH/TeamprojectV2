package com.example.herchja.teamprojectv2;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HercHja on 4/26/17.
 */

public class SharedPreferencesHandler {

    public static Set<String> saveArray(ArrayList<String> u, Context c)
    {
        Set<String> saveSetGC = new HashSet<String>(u);

        SharedPreferences pref = c.getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("MySet", saveSetGC);
        editor.commit();

        return saveSetGC;
    }


}
