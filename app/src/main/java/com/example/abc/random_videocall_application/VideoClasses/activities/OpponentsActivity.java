package com.example.abc.random_videocall_application.VideoClasses.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.abc.random_videocall_application.Home2;
import com.example.abc.random_videocall_application.R;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.Toaster;
import com.example.abc.random_videocall_application.VideoClasses.adapters.OpponentsAdapter;
import com.example.abc.random_videocall_application.VideoClasses.db.QbUsersDbManager;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.example.abc.random_videocall_application.VideoClasses.utils.CollectionsUtils;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.example.abc.random_videocall_application.VideoClasses.utils.PermissionsChecker;
import com.example.abc.random_videocall_application.VideoClasses.utils.PushNotificationSender;
import com.example.abc.random_videocall_application.VideoClasses.utils.UsersUtils;
import com.example.abc.random_videocall_application.VideoClasses.utils.WebRtcSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * QuickBlox team
 */
public class OpponentsActivity extends BaseActivity {
    private static final String TAG = OpponentsActivity.class.getSimpleName();

    private static final long ON_ITEM_CLICK_DELAY = TimeUnit.SECONDS.toMillis(10);

    private OpponentsAdapter opponentsAdapter;
    private ListView opponentsListView;
    private QBUser currentUser;
    private ArrayList<QBUser> currentOpponentsList;
    private QbUsersDbManager dbManager;
    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;
    SharedPreferences sharedPreferences;
    private PermissionsChecker checker;
    private Button retry;

    public static void start(Context context, boolean isRunForCall) {
        Intent intent = new Intent(context, OpponentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), Home2.class);
        startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_opponents);


        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        initFields();

        initDefaultActionBar();

        initUi();

        startLoadUsers();

        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(OpponentsActivity.this, true);
        }

        checker = new PermissionsChecker(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUsersList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(OpponentsActivity.this, true);
            }
        }
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_opponents);
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void initFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }

        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }

    private void startLoadUsers() {
        showProgressDialog(R.string.dlg_loading_opponents);
        String currentRoomName = SharedPrefsHelper.getInstance().get("chatUser");
        requestExecutor.loadUsersByTag(currentRoomName, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                hideProgressDialog();
                dbManager.saveAllUsers(result, true);
                initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
                showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadUsers();
                    }
                });
            }
        });
    }

    private void initUi() {
        opponentsListView = (ListView) findViewById(R.id.list_opponents);
        opponentsListView.setVisibility(View.GONE);
        retry= findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = 0;
                i = currentOpponentsList.size();
                if(i>0) {

                    int random = 0 + (int) (Math.random() * ((i - 1) + 1));
                    //tempList.add(currentOpponentsList.get(random));
                    opponentsListView.clearChoices();

                    opponentsAdapter.selectItem(random);
                    opponentsAdapter.notifyDataSetChanged();
                    hideProgressDialog();
                    videoCallfunction();
                }
            }
        });

    }

    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void initUsersList() {
//      checking whether currentOpponentsList is actual, if yes - return
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
        proceedInitUsersList();
    }

    private void proceedInitUsersList() {
        ArrayList<QBUser> tempList = new ArrayList<>();
        int i = 0;

        currentOpponentsList = dbManager.getAllUsers();
        Log.d(TAG, "proceedInitUsersList currentOpponentsList= " + currentOpponentsList);
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
        long currentTime = System.currentTimeMillis();
        for(i=0;i<currentOpponentsList.size();i++)
        {
            QBUser user=(QBUser) currentOpponentsList.get(i);
            long userLastRequestAtTime = user.getLastRequestAt().getTime();
            if((currentTime - userLastRequestAtTime) > 5*60*1000){
                // user is offline now
            }else{
                tempList.add(user);
            }

        }


        Log.e("check","");

        i = tempList.size();


        //long userLastRequestAtTime = c.getLastRequestAt().getTime();
        int random = 0 + (int)(Math.random() * ((i - 1) + 1));
        //tempList.add(currentOpponentsList.get(random));
        if(i>0) {
            opponentsAdapter = new OpponentsAdapter(this, tempList);
            opponentsAdapter.selectItem(random);
            hideProgressDialog();

            opponentsAdapter.setSelectedItemsCountsChangedListener(new OpponentsAdapter.SelectedItemsCountsChangedListener() {
                @Override
                public void onCountSelectedItemsChanged(int count) {
                    updateActionBar(count);
                }
            });

            opponentsListView.setAdapter(opponentsAdapter);
            videoCallfunction();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (opponentsAdapter != null && !opponentsAdapter.getSelectedItems().isEmpty()) {
            getMenuInflater().inflate(R.menu.activity_selected_opponents, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_opponents, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }
    public void videoCallfunction(){
        if (isLoggedInChat()) {
            startCall(true);
        }
        if (checker.lacksPermissions(Consts.PERMISSIONS)) {
            startPermissionsActivity(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.update_opponents_list:
                //startLoadUsers();
                return true;

            case R.id.settings:
                //showSettings();
                return true;

            case R.id.log_out:
                //logOut();
                return true;

            case R.id.start_video_call:
                //videoCallfunction();
                return true;

            case R.id.start_audio_call:
                if (isLoggedInChat()) {
                    startCall(false);
                }
                if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                    startPermissionsActivity(true);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void showSettings() {
        SettingsActivity.start(this);
    }

    private void startCall(boolean isVideoCall) {
        if (opponentsAdapter.getSelectedItems().size() > Consts.MAX_OPPONENTS_COUNT) {
            Toaster.longToast(String.format(getString(R.string.error_max_opponents_count),
                    Consts.MAX_OPPONENTS_COUNT));
            return;
        }

        Log.d(TAG, "startCall()");
        ArrayList<Integer> opponentsList = CollectionsUtils.getIdsSelectedOpponents(opponentsAdapter.getSelectedItems());
        ArrayList<Integer> opponentsListTemp = new ArrayList<>();
        opponentsListTemp.add(opponentsList.get(0));
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsListTemp, conferenceType);

        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsListTemp, currentUser.getFullName());

        CallActivity.start(this, false);
        Log.d(TAG, "conferenceType = " + conferenceType);
    }

    private void initActionBarWithSelectedUsers(int countSelectedUsers) {
        setActionBarTitle(String.format(getString(
                countSelectedUsers > 1
                        ? R.string.tile_many_users_selected
                        : R.string.title_one_user_selected),
                countSelectedUsers));
    }

    private void updateActionBar(int countSelectedUsers) {
        if (countSelectedUsers < 1) {
            initDefaultActionBar();
        } else {
            removeActionbarSubTitle();
            initActionBarWithSelectedUsers(countSelectedUsers);
        }

        invalidateOptionsMenu();
    }

    private void logOut() {
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();
        startLoginActivity();
    }

    private void startLogoutCommand() {
        CallService.logout(this);
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(this);
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d(TAG, "Current user was deleted from QB");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "Current user wasn't deleted from QB " + e);
            }
        });
    }

    private void startLoginActivity() {
        LoginActivity.start(this);
        finish();
    }
}