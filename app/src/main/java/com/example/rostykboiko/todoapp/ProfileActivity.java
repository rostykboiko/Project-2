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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity{
    public Toolbar toolbar;

    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 9001;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(null);

        TextView userName = (TextView)findViewById(R.id.userName);
        TextView userEmail = (TextView)findViewById(R.id.userEmail);
        TextView userID = (TextView)findViewById(R.id.userID);
        TextView photoUri = (TextView)findViewById(R.id.photoUri);

        if (GoogleAuth.getUserID()!=null) {
            userName.setText(GoogleAuth.getUserName());
            userEmail.setText(GoogleAuth.getUserEmail());
            userID.setText(GoogleAuth.getUserID());
            photoUri.setText(GoogleAuth.getProfileIcon().toString());

            loadBackdrop(GoogleAuth.getProfileIcon().toString());
        }

        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    private void loadBackdrop(String imageUrl) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load(imageUrl).centerCrop().into(imageView);
    }


    /** Google Sign In Start **/
//    private void googleSignIn(){
//
//        GoogleAccountCredential mCredential;
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());
//
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//
//    private void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getString(R.string.loading));
//            mProgressDialog.setIndeterminate(true);
//        }
//
//        mProgressDialog.show();
//    }
//
//    private void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.hide();
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//    }
//
//    private void handleSignInResult(GoogleSignInResult result) {
//
//        if (result.isSuccess()) {
//
//            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount acct = result.getSignInAccount();
//
//            GoogleAuth.setUserName(acct.getDisplayName());
//            GoogleAuth.setUserEmail(acct.getEmail());
//            GoogleAuth.setUserID(acct.getId());
//            GoogleAuth.setProfileIcon(acct.getPhotoUrl());
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
//    }
    /** Google Sign In End **/

}
