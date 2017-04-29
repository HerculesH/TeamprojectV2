package com.example.herchja.teamprojectv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by HercHja on 4/26/17.
 */

    public class SharedPreferencesHandler {

        String Json;

        public void saveArray(ArrayList<User> u, Context c)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            Json = gson.toJson(u);
            System.out.println(u + " input list SYSTEMCHECKQ");
            editor.putString("save1", Json);
            editor.apply();
        }

        public ArrayList<User> getArray(Context c)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            Gson gson = new Gson();
            String json = sharedPrefs.getString("save1", Json);
            Type type = new TypeToken<ArrayList<User>>() {}.getType();
            ArrayList<User> arrayList = gson.fromJson(json, type);
            System.out.println(arrayList + " Output list SYSTEMCHECKQ");

            MainActivity.listItems = arrayList;
            return arrayList;
        }

    }


