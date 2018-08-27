package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;

public class SignIn extends AppCompatActivity {
    TextView regiterNow_text;
    Button loginBtn;
    EditText userNameEditText,userPasswordEditText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;
    CheckBox agree_checkbox,checkBox;
    String email;
    boolean doubleBackToExitPressedOnce = false;
    static final String APP_ID="72405";
    static final String AUTH_KEY="zCNmPJGEkrGyseU";
    static final String AUTH_SECRET="V6nrN7Cdv2Vt2Vm";
    static final String ACCOUNT_KEY="qAx_5ERjtk6Fy_tBh1rs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userNameEditText = findViewById(R.id.userNameEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        agree_checkbox = findViewById(R.id.agree_checkbox);
        checkBox = findViewById(R.id.checkbox);

        initializeQuickBlox();
        setLoginButton();
        setRegisterNow();
        registerSession();
    }



    private void setLoginButton() {
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(checkifFieldsHaveValidValues())
{
    quickBloxValidation();
}
            }


        });
    }



    private void setRegisterNow() {
        regiterNow_text = findViewById(R.id.regiterNow_text);
        regiterNow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Register_Now.class);
                startActivity(intent);
            }
        });
    }




    private void quickBloxValidation()

    {dialog.show();
        final String User=userNameEditText.getText().toString().trim();
        final String Password= userPasswordEditText.getText().toString().trim();
        QBUser qbUser = new QBUser(User, Password);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle)
            {
                dialog.dismiss();
                editor.putString("user",User);
                editor.putString("password",Password);
                editor.putBoolean("hasLoggedIn", true);
                editor.putString("App_User","Simple_Login");
                editor.putString("ID",qbUser.getId().toString());
                editor.commit();
                qbUser.setPassword(Password);
                SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                getProfileById(qbUser.getId().toString());

            }

            @Override
            public void onError(QBResponseException e)
            {
                dialog.dismiss();
//Log.e("Login_Error",e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }


    public void getProfileById(String id) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.eq("userId", id);
        Performer<ArrayList<QBCustomObject>> performer =  QBCustomObjects.getObjects("Profile",requestBuilder);
        performer.performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if(qbCustomObjects.size()>0) {
                    String id = qbCustomObjects.get(0).getCustomObjectId();
                    HashMap<String, Object> fields = qbCustomObjects.get(0).getFields();
                    Log.e("Check",fields.get("Interested_In").toString());
                    Log.e("Check",fields.get("Gender").toString());
                    editor.putString("Interested_In",fields.get("Interested_In").toString());
                    editor.putString("Profile_Id",qbCustomObjects.get(0).getCustomObjectId());
                    editor.putString("PName",fields.get("Full_Name").toString());
                    editor.commit();
                    Intent intent=new Intent(getApplicationContext(),Home2.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("TAG","checking");
                editor.putString("Interested_In","");
                editor.putString("Profile_Id","");
                editor.putString("PName","");
                editor.commit();
                Intent intent=new Intent(getApplicationContext(),Home2.class);
                startActivity(intent);
                finish();
            }
        });

    }



    public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.set
        builder.setMessage(message);
        //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.white);
            }
        });
        dialog1.show();

    }
//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }
    private boolean checkifFieldsHaveValidValues()
    { String email,password;
        email= userNameEditText.getText().toString().trim();
        password = userPasswordEditText.getText().toString().trim();
        if(email.isEmpty()){
            userNameEditText.setError("Please enter  Username");
            return false;
        }else if (password.isEmpty()){
            userPasswordEditText.setError("Please enter  Password");
            return false;
        }if(!agree_checkbox.isChecked()) {
        agree_checkbox.setError("Please Agree Policies");
        return false;
    }
        return true;
    }

    private void initializeQuickBlox()
    {
//
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        // QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
//        final String API_DOAMIN = "https://apicustomdomain.quickblox.com";
//        final String CHAT_DOMAIN = "chatcustomdomain.quickblox.com";

        QBSettings.getInstance().setEndpoints("https://api.quickblox.com", "chat.quickblox.com", ServiceZone.PRODUCTION);
        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
    }

    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
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

}






