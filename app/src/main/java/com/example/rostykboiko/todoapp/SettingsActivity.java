package com.example.rostykboiko.todoapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

public class SettingsActivity extends AppCompatActivity {
    public Toolbar toolbar;
    private ListView list_settings;
    private android.widget.ListView mylist;
    private SimpleCursorAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list_settings = (ListView)findViewById(R.id.list_settings);
        setSupportActionBar(toolbar);



    }



}
