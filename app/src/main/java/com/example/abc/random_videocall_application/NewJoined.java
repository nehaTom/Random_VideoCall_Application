package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class NewJoined extends AppCompatActivity implements QBSystemMessageListener,QBChatDialogMessageListener {

    GridView androidGridView;
    ImageView home, newUser, existingUser, chatList, contact,home_white, newUser_white, existingUser_white,
            chatList_white, contact_white;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean doubleBackToExitPressedOnce = false;
    String[] gridViewString = {
            "Name","Name","Name",
            "Name","Name","Name",
            "Name","Name","Name",
            "Name","Name","Name",
            "Name","Name","Name",
    } ;
    int[] gridViewImageId = {
            R.drawable.profile, R.drawable.profile, R.drawable.profile,
            R.drawable.profile, R.drawable.profile, R.drawable.profile,
            R.drawable.profile, R.drawable.profile, R.drawable.profile,
            R.drawable.profile, R.drawable.profile, R.drawable.profile,
            R.drawable.profile, R.drawable.profile, R.drawable.profile
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_joined);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setAddMob();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        createSessionForChat();

//        Toolbar  logout=findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //setLogout();
//            }
//        });
        setGridView();
        setData();
        setOnClicks();


    }

    private void createSessionForChat()
    {
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String user,password;
        user=sharedPreferences.getString("user","");
        password=sharedPreferences.getString("password","");


        ///Load All User and save cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


        final QBUser qbUser=new QBUser(user,password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(String.valueOf(BaseService.getBaseService().getToken()));
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {

                        mDialog.dismiss();

                        QBSystemMessagesManager qbSystemMessagesManager=QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(NewJoined.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager= QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(NewJoined.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        mDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                mDialog.dismiss();
            }
        });
    }

    private void setLogout()

    {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                        Toast.makeText(NewJoined.this,"You Are Logout !!! ",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(NewJoined.this,New_Login.class);
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
        newUser.setVisibility(View.GONE);
        newUser_white.setVisibility(View.VISIBLE);
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
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                contact.setBackgroundColor(Color.parseColor("#ffffff"));
//                contact.setImageResource(R.drawable.contacts);

                contact.setVisibility(View.GONE);
                contact_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), My_Contacts.class);
                startActivity(intent);
            }
        });
    }
    private void setGridView()
    {

        getUserList();

    }






    private void getUserList()
    {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(100);

        Bundle params = new Bundle();

        QBUsers.getUsers(pagedRequestBuilder, params).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                Log.e("Users: ", users.toString());
                QBUsersHolder.getInstance().putUsers(users);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                int i=1;
                for (QBUser user: users){
                    if (!user.getLogin().equals(sharedPreferences.getString("user",""))){//(QBChatService.getInstance().getUser().getLogin())){
                        qbUserWithoutCurrent.add(user);
                        Log.d("myTag", "retrieve " + i);
                    }
                }
                //setDaaToAdapter(qbUserWithoutCurrent);
                NewJoined_GridView adapterViewAndroid = new NewJoined_GridView(NewJoined.this, qbUserWithoutCurrent);
                androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
                androidGridView.setAdapter(adapterViewAndroid);
            }
            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }

    private void setDaaToAdapter(ArrayList<QBUser> users) {
        String[] names = new String[users.size()];
        for(int i = 0;i<users.size();i++){
            QBUser qbUser = new QBUser();
            qbUser=users.get(i);
            names[i] = qbUser.getFullName();
        }
        //NewJoined_GridView adapterViewAndroid = new NewJoined_GridView(NewJoined.this, names, gridViewImageId);
        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        //androidGridView.setAdapter(adapterViewAndroid);


//        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int i, long id) {
//            }
//        });




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


    private void setAddMob()
    {
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

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {

    }
}
