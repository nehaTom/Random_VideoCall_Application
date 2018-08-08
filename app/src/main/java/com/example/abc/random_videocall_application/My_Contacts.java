package com.example.abc.random_videocall_application;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

public class My_Contacts extends AppCompatActivity {
    RecyclerView Existing_RecyclerView;
    LinearLayoutManager layoutManager;
    ExistingUser_Card_Adapter adapter;
    ImageView home, newUser, existingUser, chatList, contact,home_white, newUser_white, existingUser_white,
            chatList_white, contact_white;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Toolbar  logout=findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // setLogout();
//            }
//        });
        setRecyclerView();
        setData();
        setOnClicks();
        getRecyclerViewId();


    }

    private void setLogout()

    {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                        Toast.makeText(My_Contacts.this,"You Are Logout !!! ",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(My_Contacts.this,New_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

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
                Intent intent = new Intent(getApplication(), Home.class);
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


        private void setRecyclerView ()
        {
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }
    }
