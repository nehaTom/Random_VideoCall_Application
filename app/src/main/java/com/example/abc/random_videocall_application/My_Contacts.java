package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.random_videocall_application.VideoClasses.LogOutClass;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

public class My_Contacts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView Existing_RecyclerView;
    LinearLayoutManager layoutManager;
    ExistingUser_Card_Adapter adapter;
    ImageView home, newUser, existingUser, chatList, contact, home_white, newUser_white, existingUser_white,
            chatList_white, contact_white, logout;
  SharedPrefsHelper sharedPrefsHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
    TextView user_name_appmenu;
    boolean doubleBackToExitPressedOnce = false;
    int[] images = {
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background
    };

    //    Footer footer=new Footer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        user_name_appmenu = navigationView.getHeaderView(0).findViewById(R.id.user_name_appmenu);
//        setUserName();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        setAddMob();

        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();

//        logout=findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               setLogout();
//            }
//        });
        setRecyclerView();
        setData();
        setOnClicks();
        getRecyclerViewId();


    }
//    private void setUserName() {
//        String name = sharedPreferences.getString("PName", "");
//    }
    private void setLogout() {
        editor.putBoolean("hasLoggedIn", false);
        editor.commit();
        LogOutClass logOutClass = new LogOutClass(this, sharedPrefsHelper.getQbUser());
        logOutClass.logout();
finish();
    }

    private void getRecyclerViewId() {
    }

    private void setData() {
        home = findViewById(R.id.home);
        newUser = findViewById(R.id.newUser);
        existingUser = findViewById(R.id.existingUser);
        chatList = findViewById(R.id.chatList);
        contact = findViewById(R.id.contact);

        newUser_white = findViewById(R.id.newUser_white);
        existingUser_white = findViewById(R.id.existingUser_white);
        chatList_white = findViewById(R.id.chatList_white);
        home_white = findViewById(R.id.home_white);
        contact_white = findViewById(R.id.contact_white);
        contact.setVisibility(View.GONE);
        contact_white.setVisibility(View.VISIBLE);
    }


    private void setOnClicks() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                home.setBackgroundColor(Color.parseColor("#ffffff"));
//                home.setImageResource(R.drawable.home);
                home.setVisibility(View.GONE);
                home_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), Home2.class);
                startActivity(intent);
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                newUser.setBackgroundColor(Color.parseColor("#ffffff"));
//                newUser.setImageResource(R.drawable.newlyadded);

                newUser.setVisibility(View.GONE);
                newUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), NewJoined.class);
                startActivity(intent);
            }
        });

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                existingUser.setBackgroundColor(Color.parseColor("#ffffff"));
//                existingUser.setImageResource(R.drawable.existinguser);

                existingUser.setVisibility(View.GONE);
                existingUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), list_user_activity.class);
                startActivity(intent);
            }
        });
        chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                chatList.setBackgroundColor(Color.parseColor("#ffffff"));
//                chatList.setImageResource(R.drawable.chat);

                chatList.setVisibility(View.GONE);
                chatList_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), ChatDialogsActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setRecyclerView() {
        Existing_RecyclerView = findViewById(R.id.Existing_RecyclerView);
//        Existing_RecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Existing_RecyclerView.setLayoutManager(layoutManager);
        adapter = new ExistingUser_Card_Adapter(this, "My_Contact", images);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        Existing_RecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels, "list"));
        Existing_RecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private void setAddMob() {
        AdView mAdView;
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), Home2.class);
            startActivity(intent);
        } else if (id == R.id.profile) {
            Intent i = new Intent(getApplicationContext(), ProfileView.class);
            startActivity(i);

        } else if (id == R.id.newelyadded) {
            Intent i = new Intent(getApplicationContext(),  NewJoined.class);
            startActivity(i);

        } else if (id == R.id.existingUser) {
            Intent i = new Intent(getApplicationContext(), list_user_activity.class);
            startActivity(i);

        }else if (id == R.id.chat) {
            Intent i = new Intent(getApplicationContext(),  ChatDialogsActivity.class);
            startActivity(i);

        }else if (id == R.id.mycontacts) {
            Intent i = new Intent(getApplicationContext(),  My_Contacts.class);
            startActivity(i);

        }else if (id == R.id.logout) {
            setLogout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
