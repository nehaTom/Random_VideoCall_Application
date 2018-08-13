package com.example.abc.random_videocall_application.VideoClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.abc.random_videocall_application.Home;
import com.example.abc.random_videocall_application.New_Login;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.example.abc.random_videocall_application.VideoClasses.util.App;
import com.example.abc.random_videocall_application.VideoClasses.util.QBResRequestExecutor;
import com.example.abc.random_videocall_application.VideoClasses.utils.UsersUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.model.QBUser;

import static org.webrtc.ContextUtils.getApplicationContext;

public class LogOutClass {
    QBUser user;
    Context context;
    QBResRequestExecutor requestExecutor;

    public LogOutClass(Context context, QBUser user){
        this.user = user;
        this.context = context;
        requestExecutor = App.getInstance().getQbResRequestExecutor();
    }

    public void  logout(){
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();
        startLoginActivity();

    }

    private void startLoginActivity() {
        Toast.makeText(context,"You Are Logout !!! ",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(context,New_Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }


    private void startLogoutCommand() {
        CallService.logout(context);
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(context);
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d("tag", "Current user was deleted from QB");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("tag", "Current user wasn't deleted from QB " + e);
            }
        });
    }

}
