package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.facebook.login.Login;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {
    TextView regiterNow_text;
    Button loginBtn;
    EditText userNameEditText,userPasswordEditText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;
    CheckBox agree_checkbox,checkBox;
    Map<String, String> header1;
    String email, password;


//    static final String APP_ID="72648";
//    static final String AUTH_KEY="5ZyyFJzKUdh2kjN";
//    static final String AUTH_SECRET="wJaJMKJqqq5bznN";
//    static final String ACCOUNT_KEY="jmDTjvqNm4zi2JfyrTYm";

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
        //userNameEditText.setText(sharedPreferences.getString("userName",""));
        //userPasswordEditText.setText(sharedPreferences.getString("password",""));




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
                if(checkifFieldsHaveValidValues()) {
                    String email, password;
                    email = userNameEditText.getText().toString().trim();
                    password = userPasswordEditText.getText().toString().trim();
                    if (checkBox.isChecked()) {
                        editor.putString("userName", email);
                        editor.putString("password", password);
                        editor.apply();
                    }

                }
                quickBloxValidation();
                // loginApiCall();
            }
            // }


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


    public void loginApiCall() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        email = userNameEditText.getText().toString().trim();
        password = userPasswordEditText.getText().toString().trim();
        String url =  "http://192.168.31.180:8888/LoginAPI/REST/WebService/login";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("my app", "123"+response);

                        String str[]=response.split(":");
                        Log.e(str[0],str[1]);
                        //Map<String, String> res = response;
                        if(response != null){
                            try {
                                JSONObject resobj = new JSONObject(response);
                                if(resobj.has("success")) {
                                    String userId = resobj.getString("success");
                                    editor.putString("USER_ID",userId);
                                    editor.apply();
                                    quickBloxValidation();
                                    Intent i = new Intent(SignIn.this, Home.class);
                                    startActivity(i);
                                }
                                else {
                                    showMessage("Error", "Wrong Username or password");
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }



                        }else {
                            showMessage("Error","Wrong Username or password");
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("my app1","error");

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emailId", email);
                params.put("password",password);
                return params;
            }

        };
        requestQueue.add(jsonObjRequest);
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
                editor.commit();
                qbUser.setPassword(Password);
                SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                Intent intent=new Intent(getApplicationContext(),Home.class);
//            intent.putExtra("user",User);
//            intent.putExtra("password",Password);
                startActivity(intent);



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
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
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

}






