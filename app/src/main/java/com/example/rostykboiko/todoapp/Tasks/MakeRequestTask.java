package com.example.rostykboiko.todoapp.Tasks;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
private com.google.api.services.calendar.Calendar mService = null;
private Exception mLastError = null;

public MakeRequestTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential)
        .setApplicationName("Google Calendar API Android Quickstart")
        .build();
        }

/**
 * Background task to call Google Calendar API.
 *
 * @param params no parameters needed for this task.
 */
@Override
protected List<String> doInBackground(Void... params) {
        try {
        return getDataFromApi();
        } catch (Exception e) {
        mLastError = e;
        cancel(true);
        return null;
        }
}

/**
 * Fetch a list of the next 10 events from the primary calendar.
 *
 * @return List of Strings describing returned events.
 * @throws java.io.IOException
 */
private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService
        .events()
        .list("primary")
        .setMaxResults(100)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
        DateTime start = event.getStart().getDateTime();
        if (start == null) {
        // All-day events don't have start times, so just use
        // the start date.
        start = event.getStart().getDate();
        }
        eventStrings.add(String.format("%s", event.getEnd().toString()));
        }
        return eventStrings;
        }
}
