package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

public class New_Login extends AppCompatActivity {
    TextView login_textview;
    LoginButton FacebookLogin;
    SignInButton sign_in_button;
    CallbackManager callbackManager;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;
    SharedPreferences.Editor editor;

    static final String APP_ID="72405";
    static final String AUTH_KEY="zCNmPJGEkrGyseU";
    static final String AUTH_SECRET="V6nrN7Cdv2Vt2Vm";
    static final String ACCOUNT_KEY="qAx_5ERjtk6Fy_tBh1rs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__login);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
        SignInButton signInButton = findViewById(R.id.gmail_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        initializeQuickBlox();
        registerSession();
        setData();
        setfacebookLogin();
        setGoogleLogin();
        setLoginOnClick();

    }

    private void updateUI(GoogleSignInAccount account) {
    }


    private void setData()
    {
        login_textview=findViewById(R.id.login_textview);
        FacebookLogin =findViewById(R.id.facebook_login_button);
    }


    private void setLoginOnClick()
    {
        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplication(),SignIn.class);
                startActivity(intent);
            }
        });
    }

    private void setfacebookLogin()
    {
        callbackManager = CallbackManager.Factory.create();
        FacebookLogin.setReadPermissions("email");
        FacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("Facebook_Success",loginResult.toString());
                //getUserDetails(loginResult);
                String Token=  loginResult.getAccessToken().getToken();
               //session maintenance
                QBSessionManager.getInstance().createActiveSession(Token,null);
                String facebookAccessToken = Token;

                StringifyArrayList<String> Tag_Name = new StringifyArrayList<String>();
                Tag_Name.add("chatUser");
                QBUser user=new QBUser();
                user.setTags(Tag_Name);




                QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK,  facebookAccessToken, null).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {

//                        user.setPassword("1234567890");
//                        editor.putString("user",facebookAccessToken);
//                        editor.putString("password","1234567890");
                        editor.commit();
                        Intent intent=new Intent(getApplicationContext(),Home.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onError(QBResponseException errors)
                    {

                        Log.e("Facebook_Error",errors.toString());


                    }
                });

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("Facebook_Error",exception.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        intent.putExtra("userProfile", json_object.toString());
                        startActivity(intent);
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private void setGoogleLogin()
    {
        SignInButton signInButton = findViewById(R.id.gmail_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

            }
        });
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


}



