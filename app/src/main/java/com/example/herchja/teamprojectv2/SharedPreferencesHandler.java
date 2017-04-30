package com.example.herchja.teamprojectv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by HercHja on 4/26/17.
 */

    public class SharedPreferencesHandler {

        String Json;

        public void saveArray(ArrayList<User> u, SharedPreferences.Editor edit,String savekey)
        {
            Gson gson = new Gson();

            Json = gson.toJson(u);
            MainActivity.save = Json;
            edit.clear();
            edit.putString(savekey, Json);
            edit.commit();
        }

        public ArrayList<User> getArray(Context c, String getkey)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            Gson gson = new Gson();
            String json = sharedPrefs.getString(getkey, "");
            Type type = new TypeToken<ArrayList<User>>() {}.getType();
            ArrayList<User> arrayList = gson.fromJson(json, type);

            return arrayList;
        }

    }


