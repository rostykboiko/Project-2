package com.example.rostykboiko.todoapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rostykboiko.todoapp.Tasks.MakeRequestTask;
import com.example.rostykboiko.todoapp.adapter.GoogleAuth;
// NavDrawer
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

// Calendar


import java.util.Arrays;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    GoogleAccountCredential mCredential;
    private Menu menu;
    public Toolbar toolbar;
    private ImageView drawerButton;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;

    private Date date = new Date();
    private Drawer mDrawer = null;

    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 9001;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        final Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerButton = (ImageView) findViewById(R.id.drawerBtn);
        drawerButton.setOnClickListener(Global_OnClickListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());




        initNavDrawer();
        initCoordinationLayout();
    }

    private void googleSignIn(){

    }

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
        initNavDrawer();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        initNavDrawer();
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

    private void signIn() {
        googleSignIn();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            GoogleAuth.setUserName(acct.getDisplayName());
            GoogleAuth.setUserEmail(acct.getEmail());
            GoogleAuth.setUserID(acct.getId());
            GoogleAuth.setProfileIcon(acct.getPhotoUrl());
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

    private AccountHeader initNawDrawerHeader() {
        //          Image Download
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
        AccountHeader mHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(GoogleAuth.getUserName())
                                .withEmail(GoogleAuth.getUserEmail())
                                .withIcon(GoogleAuth.getProfileIcon())
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        // do something with the clicked item :D
                                        mDrawer.closeDrawer();
                                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                        return true;
                                    }
                                }),
                new ProfileSettingDrawerItem()
                        .withName("Add Account")
                        .withDescription("Add new GitHub Account")
                        .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus)
                                .actionBar()
                                .paddingDp(5)
                                .colorRes(R.color.monsoon)),
                new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        return mHeader;
    }

    private void initNavDrawer() {
        AccountHeader mHeader = initNawDrawerHeader();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(275)
                .withAccountHeader(mHeader)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Home")
                                .withIcon(R.drawable.ic_material_home)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                      //  result.closeDrawer();
//                                Intent intent = new Intent(MainActivity.this, Activity.class);
//                                startActivity(intent);
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withName("Calendar")
                                .withIcon(R.drawable.ic_material_calendar)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        mDrawer.closeDrawer();
                                        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                                        startActivity(intent);
                                        return false;
                                    }
                                }),
                        new SecondaryDrawerItem()
                                .withName("Sign In")
                                .withIcon(R.drawable.ic_material_accsircle)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        mDrawer.closeDrawer();
                                        signIn();
                                        return false;
                                    }}),
                        new SectionDrawerItem()
                                .withName("Groups"),
                        new PrimaryDrawerItem()
                                .withName("Main group")
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(
                                            View view, int position, IDrawerItem drawerItem) {
                                        mDrawer.closeDrawer();
                                        // do something with  this peace of sh**
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withName("Add new Item")
                                .withIcon(R.drawable.ic_material_addgroup)
                                .withOnDrawerItemClickListener
                                        (new Drawer.OnDrawerItemClickListener() {
                                             @Override
                                             public boolean onItemClick(View view, int position,
                                                                        IDrawerItem drawerItem) {
                                                 mDrawer.removeItem(position);
                                                 addNewDrawerItem(position);
                                                 return false;
                                             }
                                         })
                )
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                            .withName("Sign In")
                            .withIcon(R.drawable.ic_material_accsircle)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    mDrawer.closeDrawer();
                                    signIn();
                                    return false;
                                }}),
                        new SecondaryDrawerItem()
                                .withName("Settings")
                                .withIcon(R.drawable.ic_material_cog)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                        return false;
                                    }
                                })
                )
                .build();

    }

    private void addNewDrawerItem(int position) {
        mDrawer.getDrawerItem(position);
        mDrawer.addItem(new PrimaryDrawerItem().withName("newItem").withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mDrawer.removeItem(position);
                        return false;
                    }
                })
        );
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() != R.id.btnadd){
            return super.onOptionsItemSelected(item);
        } else  {
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id", 0);
            Intent intent = new Intent(getApplicationContext(),
                    EditorActivity.class);
            intent.putExtras(dataBundle);
            startActivity(intent);
            finish();
            return true;
        }
    }

    private void initCoordinationLayout() {
        FloatingActionButton btnadd = (FloatingActionButton) findViewById(R.id.btnadd);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(),
                        EditorActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });


        CalendarDB mydb = new CalendarDB(this);
        Cursor c = mydb.fetchAll();


        String[] fieldNames = new String[]{CalendarDB.name, CalendarDB._id,
                CalendarDB.dataStart, CalendarDB.description};

        int[] display = new int[]{R.id.txtNameRow, R.id.txtIDrow,
                R.id.txtTimeRow, R.id.txtDescription};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_event_item, c, fieldNames,
                display, 0);

        android.widget.ListView mylist = (ListView) findViewById(R.id.listView1);

        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                LinearLayout linearLayoutParent = (LinearLayout) arg1;
                LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent
                        .getChildAt(0);
                TextView m = (TextView) linearLayoutChild.getChildAt(2);
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id",
                        Integer.parseInt(m.getText().toString()));
                Intent intent = new Intent(getApplicationContext(),
                        EditorActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });

    }

    private String setDate() {
        int todaysday = date.getDay();
        int todaysdate = date.getDate();
        String dateString = "";

        if (todaysday == 1) {
            dateString = todaysdate + ", " + getText(R.string.monday);
        }
        if (todaysday == 2) {
            dateString = todaysdate + ", " + getText(R.string.tuesday);
        }
        if (todaysday == 3) {
            dateString = todaysdate + ", " + getText(R.string.wednesday);
        }
        if (todaysday == 4) {
            dateString = todaysdate + ", " + getText(R.string.thursday);
        }
        if (todaysday == 5) {
            dateString = todaysdate + ", " + getText(R.string.friday);
        }
        if (todaysday == 6) {
            dateString = todaysdate + ", " + getText(R.string.saturday);
        }
        if (todaysday == 7) {
            dateString = todaysdate + ", " + getText(R.string.sunday);
        }
        return dateString;
    }


    private final View.OnClickListener Global_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId() == R.id.drawerBtn)
                mDrawer.openDrawer();
        }
    };


    /**
     * Google Calendar Sync Start
     **/
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(MainActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    /** Google Calendar Sync End**/
}
