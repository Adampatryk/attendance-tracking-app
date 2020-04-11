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
        if (!user.hasToken()) return false;
        if (user.getAuthorisation_error() != null) return false;
        if (user.getId() == -1) return false;

        //TODO Make a request with the token to check if it is valid
        return true;
    }

    public static UserModel getUser() {
        return user;
    }

    public static boolean login(UserModel userRequest, UserModel userResponse){

        //Check if the users match
        if (!userRequest.getUsername().equals(userResponse.getUsername())){
            Log.d(TAG, "login: usernames don't match\n\nRequest username: " + userRequest.getUsername() + "\nResponse username: " + userResponse.getUsername());
            return false;
        }

        SessionManager.user = userResponse;

        Log.d(TAG, "login: Logging user in " + user.toString());
        if (sp != null) {

            sp.edit().putInt("id", user.getId()).apply();
            sp.edit().putString("username", user.getUsername()).apply();
            sp.edit().putString("password", user.getPassword()).apply();
            sp.edit().putBoolean("lecturer", user.isLecturer()).apply();
            sp.edit().putString("token", user.getToken(false)).apply();
        } {
            Log.d(TAG, "login: Logging in without shared preferences");
        }

        return isAuthenticated();
    }

    public static boolean logout(){
        Log.d(TAG, "logout: Logging out");
        SessionManager.user = null;

        if (sp != null) {
            sp.edit().putInt("id", -1).apply();
            sp.edit().putString("username", null).apply();
            sp.edit().putString("password", null).apply();
            sp.edit().putBoolean("lecturer", false).apply();
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
                sp.getInt("id", -1),
                sp.getString("username", null),
                sp.getString("password", null),
                sp.getString("token", null),
                sp.getBoolean("lecturer", false),
                null
        );
    }

    public static boolean hasSharedPreferences(){
        return sp != null;
    }
}
