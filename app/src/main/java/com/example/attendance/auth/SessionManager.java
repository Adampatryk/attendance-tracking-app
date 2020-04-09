package com.example.attendance.auth;

import android.content.SharedPreferences;
import android.se.omapi.Session;
import android.util.Log;

import com.example.attendance.models.UserModel;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static UserModel user = null;
    private static SharedPreferences sp = null;

    public static boolean isAuthenticated(){
        if (user == null) return false;
        if (user.getToken() == null) return false;
        if (user.getAuthorisation_error() != null) return false;
        return true;
    }

    public static UserModel getUser() {
        return user;
    }

    public static boolean login(UserModel user){
        Log.d(TAG, "login: Logging user in" + user.toString());
        SessionManager.user = user;

        if (sp != null) {
            Log.d(TAG, "login: Logging in without shared preferences");
            sp.edit().putString("username", user.getUsername()).apply();
            sp.edit().putString("password", user.getPassword()).apply();
            sp.edit().putString("token", user.getToken()).apply();
        }

        return isAuthenticated();

    }

    public static boolean logout(){
        Log.d(TAG, "logout: Logging out");
        SessionManager.user = null;

        if (sp != null) {
            sp.edit().putString("username", null).apply();
            sp.edit().putString("password", null).apply();
            sp.edit().putString("token", null).apply();
        }
        else {
            Log.d(TAG, "logout: Logging out without shared preferences");
            return false;
        }

        return true;
    }

    public static void setSharedPreferences(SharedPreferences sp){
        Log.d(TAG, "setSharedPreferences: Setting shared preferences");
        SessionManager.sp = sp;

        SessionManager.user = new UserModel(
                sp.getString("username", null),
                sp.getString("password", null),
                sp.getString("token", null),
                null
        );
    }

    public static boolean hasSharedPreferences(){
        return sp != null;
    }
}
