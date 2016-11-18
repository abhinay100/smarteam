package com.ellipsonic.smartteam.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;

/**
 * Created by USER on 10/17/2016.
 */
public class SharedPreference {

    public static final String PREFS_FILE_NAME = "AOP_PREFS";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PHOTO = "USER_PHOTO";

    public SharedPreference() {
        super();
    }

    public void saveGmailDetails(Context context, String user_name, String user_email , String photo_url) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(USER_NAME, user_name); //1
        editor.putString(USER_EMAIL, user_email); //2
        editor.putString(USER_PHOTO, photo_url); //3

        editor.commit(); //4
    }
    public void saveUserId(Context context, String user_id) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(USER_ID, user_id); //3

        editor.commit(); //4
    }

    public String getValue(Context context,String key) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        text = settings.getString(key, null);
        return text;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context,String key) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(key);
        editor.commit();
    }
}
