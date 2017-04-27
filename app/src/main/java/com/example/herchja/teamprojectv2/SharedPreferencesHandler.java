package com.example.herchja.teamprojectv2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HercHja on 4/26/17.
 */

public class SharedPreferencesHandler {

    public static SharedPreferences.Editor prefsEditor;
    public static ArrayList<User> usave = new ArrayList<User>();
    Gson gson;

    public void saveArray(ArrayList<User> u, SharedPreferences p)
    {
        prefsEditor = p.edit();
        Gson gson = new Gson();
        String json = gson.toJson(u);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    public ArrayList<User> getArray(SharedPreferences p)
    {
        gson = new Gson();
        String json = p.getString("MyObject", "");
        usave = gson.fromJson(json, new TypeToken<ArrayList<User>>() {}.getType());
        return usave;
    }

}
