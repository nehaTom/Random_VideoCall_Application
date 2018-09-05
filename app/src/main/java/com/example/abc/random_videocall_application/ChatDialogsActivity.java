package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.random_videocall_application.VideoClasses.LogOutClass;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.Toaster;
import com.example.abc.random_videocall_application.VideoClasses.activities.CallActivity;
import com.example.abc.random_videocall_application.VideoClasses.activities.PermissionsActivity;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.example.abc.random_videocall_application.VideoClasses.utils.PermissionsChecker;
import com.example.abc.random_videocall_application.VideoClasses.utils.PushNotificationSender;
import com.example.abc.random_videocall_application.VideoClasses.utils.WebRtcSessionManager;
import com.github.library.bubbleview.BubbleTextView;
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
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatDialogsActivity extends AppCompatActivity implements QBSystemMessageListener, QBChatDialogMessageListener, NavigationView.OnNavigationItemSelectedListener {
    ListView lstChatDialogs;

    ImageView home, newUser, existingUser, chatList, contact, home_white, newUser_white, existingUser_white,
            chatList_white, contact_white, logout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPrefsHelper sharedPrefsHelper;
    TextView user_name_appmenu;
    Uri photoToUpload;
    private PermissionsChecker checker;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.context_delete_dialog:
                deleteDialog(info.position);
                break;
        }
        return true;
    }

    private void deleteDialog(int index) {
        ProgressDialog deleteDialog = new ProgressDialog(ChatDialogsActivity.this);
        deleteDialog.setMessage("Plese wait....");
        deleteDialog.show();
        QBChatDialog chatDialog = (QBChatDialog) lstChatDialogs.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatDialogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                ChatDialogsAdapters adapters = new ChatDialogsAdapters(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                lstChatDialogs.setAdapter(adapters);
                adapters.notifyDataSetChanged();
                loadChatDialogs();
                deleteDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogs);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        checker = new PermissionsChecker(getApplicationContext());
        setAddMob();

//      logout=findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {m
//                setLogout();
//            }
//        });

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        createSessionForChat();
        loadChatDialogs();
        setData();
        setOnClicks();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user_name_appmenu = navigationView.getHeaderView(0).findViewById(R.id.user_name_appmenu);
        setUserName();

        lstChatDialogs = (ListView) findViewById(R.id.lstChatDialogs);
        registerForContextMenu(lstChatDialogs);
        lstChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog) lstChatDialogs.getAdapter().getItem(position);
                String Sender_Name = qbChatDialog.getName();

                long millis=(qbChatDialog.getLastMessageDateSent())/1000;
                long m = (millis / 60) % 60;
                long h = (millis / (60 * 60))%24;
                String hms = String.format("%02d:%02d", h,
                        m);
               // String Last_Seen=qbChatDialog.getLastMessageDateSent();
                editor.putString("Last_Seen",hms);
                editor.putString("SenderName", Sender_Name);
                editor.commit();

                Intent intent = new Intent(ChatDialogsActivity.this, ChatMessage.class);

                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);

                intent.putExtra("Activity_Name", "Chat_Dialog");
                startActivity(intent);

            }
        });


    }



    private void setUserName() {
        String name = sharedPreferences.getString("PName", "");
        user_name_appmenu.setText(name);
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();
    }

    private void setLogout() {
      editor.putBoolean("hasLoggedIn", false);
      editor.commit();
        LogOutClass logOutClass = new LogOutClass(this, sharedPrefsHelper.getQbUser());
        logOutClass.logout();
        finish();
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


    private void createSessionForChat() {
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String user, password;
        user = sharedPreferences.getString("user", "");
        password = sharedPreferences.getString("password", "");


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


        final QBUser qbUser = new QBUser(user, password);
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

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ChatDialogsActivity.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatDialogsActivity.this);
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
    protected void onResume() {
        super.onResume();
        loadChatDialogs();
    }

    private void loadChatDialogs() {
        ProgressDialog loadChatdialog = new ProgressDialog(ChatDialogsActivity.this);
        loadChatdialog.setMessage("Plese wait....");
        loadChatdialog.show();
        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
        requestGetBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

//                ChatDialogsAdapters adapters=new ChatDialogsAdapters(getBaseContext(),qbChatDialogs);
//                adapters.notifyDataSetChanged();

                /// Unread Settings
                Set<String> setIds = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs)
                    setIds.add(chatDialog.getDialogId());


                ////// Get Message Unread
                QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle()).performAsync(new QBEntityCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer, Bundle bundle) {

                        /// save to cache
                        QBUnreadMessageHolder.getInstance().setBundle(bundle);

                        /// Refresh List Dialog

//                        ChatDialogsAdapters adapters = new ChatDialogsAdapters(getBaseContext(),QBChatDialogHolder.getInstance().getAllChatDialogs());
                        ChatDialogsAdapters adapters = new ChatDialogsAdapters(getBaseContext(), qbChatDialogs);
                        lstChatDialogs.setAdapter((adapters));
                        adapters.notifyDataSetChanged();
                        loadChatdialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("error", e.getMessage());
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

        loadChatDialogs();
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {

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
        chatList.setVisibility(View.GONE);
        chatList_white.setVisibility(View.VISIBLE);
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }
    private void startHomeActivity() {
        Home2.start(ChatDialogsActivity.this, false);
        finish();
    }
    private void setOnClicks() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.setVisibility(View.GONE);
                home_white.setVisibility(View.VISIBLE);
                startLoginService(SharedPrefsHelper.getInstance().getQbUser());
                startHomeActivity();
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser.setVisibility(View.GONE);
                newUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), NewJoined.class);
                startActivity(intent);
            }
        });

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existingUser.setVisibility(View.GONE);
                existingUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), list_user_activity.class);
                startActivity(intent);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    //--------------------------------------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home) {
            startLoginService(SharedPrefsHelper.getInstance().getQbUser());
            startHomeActivity();
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


    //--------------------------------------------



    private class ChatMessageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<QBChatMessage> qbChatMessages;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
            this.context = context;
            this.qbChatMessages = qbChatMessages;

        }

        @Override
        public int getCount() {
            return qbChatMessages.size();
        }

        @Override
        public Object getItem(int i) {
            return qbChatMessages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            if (convertView == null){

                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                Log.d("myTag", " " + qbChatMessages.get(i).getSenderId());
                Log.d("myTag", " " + QBChatService.getInstance().getUser().getId());

                if (qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())){

                    view = inflater.inflate(R.layout.list_send_message, null);
                    BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                    TextView time=view.findViewById(R.id.time);
                    TextView date=view.findViewById(R.id.date);
                    long date_value = qbChatMessages.get(i).getDateSent();
                    String dateValue = Long.toString(date_value);
                    date.setText(dateValue);
                    bubbleTextView.setText(qbChatMessages.get(i).getBody());
                    long millis=(qbChatMessages.get(i).getDateSent())/1000;
                    //long s = millis % 60;
                    long m = (millis / 60) % 60;
                    long h = (millis / (60 * 60))%24;
                    String hms = String.format("%02d:%02d", h,
                            m);

                    time.setText(hms);
                    time.setTextColor(Color.BLACK);
                    Log.e("time", String.valueOf(qbChatMessages.get(i).getDateSent()/1000));

                } else {

                    sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    view = inflater.inflate(R.layout.list_rec_message, null);
                    BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                    TextView time=view.findViewById(R.id.time);

                    TextView date=view.findViewById(R.id.date);
                    long date_value = qbChatMessages.get(i).getDateSent();
                    String dateValue = Long.toString(date_value);
                    date.setText(dateValue);
                    //date.setText((int) qbChatMessages.get(i).getDateSent());

                    bubbleTextView.setText(qbChatMessages.get(i).getBody());
                    TextView txtName = (TextView)view.findViewById(R.id.message_user);
                    txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName());
                    time.setText(""+qbChatMessages.get(i).getDateSent());
                    time.setTextColor(Color.BLACK);
                    String SenderName= QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName();

                    //editor.putString("SenderName",SenderName);
                    //editor.commit();
                }
            }
            return view;
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
                CallService.start(getApplicationContext(), qbUser);
            }
        }

        private void videoCallfunction(int position) {
            //selectedUser = (QBUser)lstUsers.getItemAtPosition(position);

            if (isLoggedInChat()) {
                startCall(true);
            }
            if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                startPermissionsActivity(false);
            }

        }

        private void startPermissionsActivity(boolean checkOnlyAudio) {
            PermissionsActivity.startActivity(getApplicationContext(), checkOnlyAudio, Consts.PERMISSIONS);
        }

        private void startCall(boolean isVideoCall) {


          //  int idValue = selectedUser.getId();
            ArrayList<Integer> opponentsList = new ArrayList<>();
           // opponentsList.add(idValue);
            QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                    ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                    : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

            QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

            WebRtcSessionManager.getInstance(getApplicationContext()).setCurrentSession(newQbRtcSession);

            PushNotificationSender.sendPushMessage(opponentsList, sharedPrefsHelper.getQbUser().getFullName());

            CallActivity.start(getApplicationContext(), false);
        }


    }


}
