package com.example.rostykboiko.todoapp;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rostykboiko.todoapp.database.GoogleAuth;
import com.example.rostykboiko.todoapp.Tasks.DownloadImageTask;

public class ProfileActivity extends AppCompatActivity {
    public TextView userName;
    public TextView userEmail;
    public TextView userID;
    public TextView photoUri;
    public Toolbar toolbar;
    public String str = "https://lh4.googleusercontent.com/-wMDm4rQcC9E/AAAAAAAAAAI/AAAAAAAAFls/n-RNq863i_o/photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Info");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (TextView)findViewById(R.id.userName);
        userEmail = (TextView)findViewById(R.id.userEmail);
        userID = (TextView)findViewById(R.id.userID);
        photoUri = (TextView)findViewById(R.id.photoUri);


        if (GoogleAuth.getUserID()!=null) {
            userName.setText(GoogleAuth.getUserName());
            userEmail.setText(GoogleAuth.getUserEmail());
            userID.setText(GoogleAuth.getUserID());
            photoUri.setText(GoogleAuth.getProfileIcon().toString());
            new DownloadImageTask((ImageView)findViewById(R.id.imageView))
                    .execute(GoogleAuth.getProfileIcon().toString());
        }


    }

}
