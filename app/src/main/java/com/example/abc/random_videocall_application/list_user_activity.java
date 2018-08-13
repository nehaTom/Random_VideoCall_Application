package com.example.abc.random_videocall_application;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.abc.random_videocall_application.VideoClasses.LogOutClass;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.Toaster;
import com.example.abc.random_videocall_application.VideoClasses.activities.CallActivity;
import com.example.abc.random_videocall_application.VideoClasses.activities.PermissionsActivity;
import com.example.abc.random_videocall_application.VideoClasses.db.QbUsersDbManager;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.example.abc.random_videocall_application.VideoClasses.utils.PermissionsChecker;
import com.example.abc.random_videocall_application.VideoClasses.utils.PushNotificationSender;
import com.example.abc.random_videocall_application.VideoClasses.utils.WebRtcSessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;

public class list_user_activity extends AppCompatActivity implements QBSystemMessageListener,QBChatDialogMessageListener {

    //    ListView lstuser;
    ListView lstUsers;
    Button btn_create_chat;
    ImageView home, newUser, existingUser, chatList, contact,home_white, newUser_white, existingUser_white,
            chatList_white, contact_white,logout;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    QBUser selectedUser;
    SharedPrefsHelper sharedPrefsHelper;
    private PermissionsChecker checker;
    boolean doubleBackToExitPressedOnce = false;
    private QbUsersDbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_activity);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        checker = new PermissionsChecker(getApplicationContext());
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        createSessionForChat();
        setAddMob();

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogout();
            }
        });
        lstUsers = (ListView)findViewById(R.id.lstuser);
        lstUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        lstUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                selectedUser = (QBUser)lstUsers.getItemAtPosition(position);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.grid_menu,
                        popupMenu.getMenu());
                popupMenu
                        .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.start_video_call:
                                        videoCallfunction(position);
                                        return true;

                                    case R.id.start_audio_call:
                                        if (isLoggedInChat()) {
                                            startCall(false);
                                        }
                                        if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                                            startPermissionsActivity(true);
                                        }
                                        return true;

                                    case R.id.start_chat:
                                        onClickChatIcon(position);
                                        return true;
                                }

                                return false;
                            }
                        });
                popupMenu.show();
            }
        });



        retrieveAllUsers();
        setData();
        setOnClicks();
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }

    private void videoCallfunction(int position) {
        //  selectedUser = (QBUser)lstUsers.getItemAtPosition(position);
        if (isLoggedInChat()) {
            startCall(true);
        }
        if (checker.lacksPermissions(Consts.PERMISSIONS)) {
            startPermissionsActivity(false);
        }

    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void startCall(boolean isVideoCall) {

        int idValue = selectedUser.getId();
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(idValue);
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, sharedPrefsHelper.getQbUser().getFullName());

        CallActivity.start(this, false);
    }

    private void onClickChatIcon(int i){

        final ProgressDialog mDialog = new ProgressDialog(list_user_activity.this);
        mDialog.setMessage("Loading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        QBUser user =(QBUser)lstUsers.getItemAtPosition(i);
        String SenderName = user.getFullName();
        editor.putString("SenderName",SenderName);
        editor.commit();
        QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();

                Intent intent=new Intent(getApplication(),ChatMessage.class);
                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                intent.putExtra("Activity_Name","List_User");
                startActivity(intent);

                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", ""+e.getMessage());
                mDialog.dismiss();
            }
        });
    }

    private void setLogout()
    {
        LogOutClass logOutClass = new LogOutClass(this,sharedPrefsHelper.getQbUser());
        logOutClass.logout();
    }


    private void retrieveAllUsers() {

        if(dbManager.getAllUsers().size()<=0) {

            final ProgressDialog mDialog = new ProgressDialog(list_user_activity.this);
            mDialog.setMessage("Loading...");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                    mDialog.dismiss();
                    dbManager.saveAllUsers(qbUsers, true);
                    setListDataValue();
                }

                @Override
                public void onError(QBResponseException e) {
                    mDialog.dismiss();
                    Log.e("ERROR", "" + e.getMessage());
                }
            });

        }else {
            setListDataValue();
        }
    }

    private void setListDataValue() {
        ArrayList<QBUser> qbUsers = dbManager.getAllUsers();
        QBUsersHolder.getInstance().putUsers(qbUsers);

        ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
        int i = 1;
        for (QBUser user : qbUsers) {

            if (!user.getLogin().equals(sharedPreferences.getString("user", ""))) {//(QBChatService.getInstance().getUser().getLogin())){
                qbUserWithoutCurrent.add(user);
                Log.d("myTag", "retrieve " + i);
            }
        }

        ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(), qbUserWithoutCurrent);
        lstUsers.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list_user_activity);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        lstuser = findViewById(R.id.lstuser);
//        lstuser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
//        btn_create_chat = findViewById(R.id.btn_create_chat);
//        btn_create_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int countChoice = lstuser.getCount();
//                if (lstuser.getCheckedItemPositions().size()==1)
//                    createPrivateChat(lstuser.getCheckedItemPositions());
//                else if (lstuser.getCheckedItemPositions().size()>1)
//                    createGroupChatt(lstuser.getCheckedItemPositions());
//                else
//                    Toast.makeText(list_user_activity.this,"Please select friend to chat",Toast.LENGTH_LONG);
//            }
//        });
//        retrieveAllUser();
//        setData();
//        setOnClicks();
//    }
//
//    private void createGroupChatt(SparseBooleanArray checkedItemPositions)
//    {
//        final ProgressDialog dialog=new ProgressDialog(list_user_activity.this);
//        dialog.setMessage("Please waiting...");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//        int countChoice=lstuser.getCount();
//        ArrayList<Integer> occupantIdsList= new ArrayList<>();
//        for (int i=0; i<countChoice;i++)
//        {
//            if (checkedItemPositions.get(i))
//            {
//                QBUser user= (QBUser) lstuser.getItemAtPosition(i);
//                occupantIdsList.add(user.getId());
//            }
//        }
//
//        ///Create Chat Dialog
//        QBChatDialog dialog1=new QBChatDialog();
//        dialog1.setName(Common.createChatDialogName(occupantIdsList));
//        dialog1.setType(QBDialogType.GROUP);
//        dialog1.setOccupantsIds(occupantIdsList);
//
//        QBRestChatService.createChatDialog(dialog1).performAsync(new QBEntityCallback<QBChatDialog>() {
//            @Override
//            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
//                dialog.dismiss();
//                Toast.makeText(getBaseContext(),"Create chat dialog successfully",Toast.LENGTH_LONG);
//                finish();
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//Log.e("GroupError",e.getMessage());
//            }
//        });
//    }
//
//    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
//
//        final ProgressDialog dialog=new ProgressDialog(list_user_activity.this);
//        dialog.setMessage("Please waiting...");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//        int countChoice=lstuser.getCount();
//        ArrayList<Integer> occupantIdsList= new ArrayList<>();
//        for (int i=0; i<countChoice;i++)
//        {
//            if (checkedItemPositions.get(i))
//            {
//                QBUser user= (QBUser) lstuser.getItemAtPosition(i);
//               QBChatDialog qbChatDialog= DialogUtils.buildPrivateDialog(user.getId());
//
//                QBRestChatService.createChatDialog(qbChatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
//                    @Override
//                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
//                        dialog.dismiss();
//                        Toast.makeText(getBaseContext(),"Create Private chat dialog successfully",Toast.LENGTH_LONG);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(QBResponseException e) {
//                        Log.e("GroupError",e.getMessage());
//                    }
//                });
//            }
//        }
//
//    }

//    private void retrieveAllUser() {
//        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
//
//            @Override
//            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle)
//            {
//                QBUsersHolder.getInstance().putUsers(qbUsers);
//
//                ArrayList<QBUser> qbUserWithoutcurrent = new ArrayList<QBUser>();
//                for (QBUser user : qbUsers)
//                {
//                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
//                    {
//                        qbUserWithoutcurrent.add(user);
//                    }
//
//                    ListUsersAdapter adapter=new ListUsersAdapter(getBaseContext(),qbUserWithoutcurrent);
//                    lstUsers.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onError(QBResponseException errors)
//            {
//                Log.e("Error",errors.getMessage());
//            }
//        });
//    }


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
        existingUser.setVisibility(View.GONE);
        existingUser_white.setVisibility(View.VISIBLE);
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
                        qbSystemMessagesManager.addSystemMessageListener(list_user_activity.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager= QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(list_user_activity.this);
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