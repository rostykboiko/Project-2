package com.example.rostykboiko.todoapp.adapter;


import android.content.Context;

import com.example.rostykboiko.todoapp.R;

public class DateAdapter {

    public static String monthOfYear(Context context, int month){
        String monthOfYear = "";

        if (month + 1 == 1) monthOfYear = context.getResources().getString(R.string.January);
        if (month + 1 == 2) monthOfYear = context.getResources().getString(R.string.February);
        if (month + 1 == 3) monthOfYear = context.getResources().getString(R.string.March);
        if (month + 1 == 4) monthOfYear = context.getResources().getString(R.string.April);
        if (month + 1 == 5) monthOfYear = context.getResources().getString(R.string.May);
        if (month + 1 == 6) monthOfYear = context.getResources().getString(R.string.June);
        if (month + 1 == 7) monthOfYear = context.getResources().getString(R.string.July);
        if (month + 1 == 8) monthOfYear = context.getResources().getString(R.string.August);
        if (month + 1 == 9) monthOfYear = context.getResources().getString(R.string.September);
        if (month + 1 == 10) monthOfYear = context.getResources().getString(R.string.October);
        if (month + 1 == 11) monthOfYear = context.getResources().getString(R.string.November);
        if (month + 1 == 12) monthOfYear = context.getResources().getString(R.string.December);

        return monthOfYear;
    }

    public static String dayOfWeekString(Context context, int dayOfWeek){
        String dayOfWeekTitle = null;
        if(dayOfWeek == 1) dayOfWeekTitle = context.getResources().getString(R.string.sun);
        if(dayOfWeek == 2) dayOfWeekTitle = context.getResources().getString(R.string.mon);
        if(dayOfWeek == 3) dayOfWeekTitle = context.getResources().getString(R.string.tue);
        if(dayOfWeek == 4) dayOfWeekTitle = context.getResources().getString(R.string.wed);
        if(dayOfWeek == 5) dayOfWeekTitle = context.getResources().getString(R.string.thu);
        if(dayOfWeek == 6) dayOfWeekTitle = context.getResources().getString(R.string.fri);
        if(dayOfWeek == 7) dayOfWeekTitle = context.getResources().getString(R.string.sat);

        return dayOfWeekTitle;
    }


}
