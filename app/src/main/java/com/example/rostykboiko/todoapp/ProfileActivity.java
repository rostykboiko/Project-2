package com.example.rostykboiko.todoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rostykboiko.todoapp.adapter.GoogleAuth;
import com.example.rostykboiko.todoapp.Tasks.DownloadImageTask;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    public Toolbar toolbar;
    private ImageView imageView;
    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(null);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ImageView backButton = (ImageView)findViewById(R.id.backBtn);
        Button signInBtn = (Button)findViewById(R.id.sign_in_button);
        Button signOutBtn = (Button)findViewById(R.id.sign_out_button);
        imageView = (ImageView)findViewById(R.id.imageView);

        backButton.setOnClickListener(Global_OnClickListener);
        signInBtn.setOnClickListener(Global_OnClickListener);
        signOutBtn.setOnClickListener(Global_OnClickListener);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void loadBackdrop(String imageUrl) {
        Glide.with(this).load(imageUrl).centerCrop().into(imageView);
    }

    /** Google SignIn Start **/
    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
        }
    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn){
        TextView userName = (TextView)findViewById(R.id.userName);
        TextView userEmail = (TextView)findViewById(R.id.userEmail);


        if (signedIn) {
            userName.setText(GoogleAuth.getUserName());
            userEmail.setText(GoogleAuth.getUserEmail());


            loadBackdrop(GoogleAuth.getProfileIcon().toString());
        } else {
            userName.setText("");
            userEmail.setText("");
            imageView.setImageResource(R.drawable.person_photo);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        updateUI(true);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
        GoogleAuth.setUserName(null);
        GoogleAuth.setUserEmail(null);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            GoogleAuth.setUserName(acct.getDisplayName());
            GoogleAuth.setUserEmail(acct.getEmail());
            GoogleAuth.setUserID(acct.getId());
            GoogleAuth.setProfileIcon(acct.getPhotoUrl());
            updateUI(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /** Google SignIn End **/

    private final View.OnClickListener Global_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if (v.getId() == R.id.sign_in_button) signIn();
            if (v.getId() == R.id.sign_out_button) signOut();
            if (v.getId() == R.id.backBtn) onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(
                getApplicationContext(),
                MainActivity.class);
        startActivity(intent);
        finish();
    }

}
