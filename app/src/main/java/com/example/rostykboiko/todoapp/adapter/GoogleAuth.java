package com.example.rostykboiko.todoapp.adapter;

import android.graphics.Bitmap;

import android.net.Uri;

public class GoogleAuth {

    public static String userName = null;
    public static String userEmail = null;
    public static String userID = null;
    public static Uri userProfileIcon;
    public static Bitmap bm = null;

    public static String getUserName(){
        return userName;
    }
    public static void setUserName(String userAuthName) {
        userName = userAuthName;
    }

    public static String getUserEmail(){
        return userEmail;
    }
    public static void setUserEmail(String userAuthEmail){
        userEmail = userAuthEmail;
    }

    public static String getUserID(){
        return userID;
    }
    public static void setUserID(String userAuthID){
        userID = userAuthID;
    }

    public static void setProfileIcon(Uri userAuthIcon){
        userProfileIcon = userAuthIcon;
    }
    public static Uri getProfileIcon(){
        return userProfileIcon;
    }

    public static long stringToData(String dataString, String timeTime){
        long data = 0;

        return data;
    }

}
