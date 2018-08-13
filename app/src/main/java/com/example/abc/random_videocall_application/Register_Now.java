package com.example.abc.random_videocall_application;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Register_Now extends AppCompatActivity {
    RadioButton maleGender,femaleGender,otherGender;
    RadioGroup genderRadioGroup;
    EditText nameEdit,mobileEdit,passwordEdit,birthEdit,emailEdit;
    Button registrationBtn;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;
    SharedPreferences.Editor editor;
    Map<String, String> header;
    String name, mobile, password, birthday, gender,email;

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
        setContentView(R.layout.activity_register__now);

        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        maleGender=findViewById(R.id.maleGender);
        femaleGender =findViewById(R.id.femaleGender);
        otherGender =findViewById(R.id.otherGender);

        nameEdit =findViewById(R.id.nameEdit);
        mobileEdit =findViewById(R.id.mobileEdit);
        passwordEdit =findViewById(R.id.passwordEdit);
        birthEdit =findViewById(R.id.birthEdit);
        emailEdit=findViewById(R.id.emailEdit);

        initializeQuickBlox();
        registerQuickBlox();



        RadioGroup rg = findViewById(R.id.genderRadioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.femaleGender:
                        gender = "F";
                        break;
                    case R.id.maleGender:
                        gender = "M";
                        break;
                    case R.id.otherGender:
                        gender = "O";
                        break;

                }
            }
        });

        birthEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Register_Now.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthEdit.setText(sdf.format(myCalendar.getTime()));


        registrationBtn = findViewById(R.id.registrationBtn);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    quickBloxValidation();
                    //RegisterationApi();
                }
            }
        });


    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.set
        builder.setMessage(message);
        //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog) dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.white);
            }
        });
        dialog1.show();

    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    private boolean checkValidation() {


        if (nameEdit.getText().toString().equals("")) {
            nameEdit.setError("field Cannot be empty");
            return false;
        } else if (emailEdit.getText().toString().equals("")) {
            emailEdit.setError("field Cannot be empty");
            return false;
        } else if (mobileEdit.getText().toString().equals("")) {
            mobileEdit.setError("field Cannot be empty");
            return false;
        } else if (passwordEdit.getText().toString().equals("")) {
            passwordEdit.setError("field Cannot be empty");
            return false;
        } else if (birthEdit.getText().toString().equals("")) {
            birthEdit.setError("field Cannot be empty");
            return false;
//        } else if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
//            otherGender.setError("field Cannot be empty");
//            return false;
        }
        return true;
    }




    private void quickBloxValidation()

    {
        dialog.show();
        StringifyArrayList<String> Tag_Name = new StringifyArrayList<String>();
        Tag_Name.add("chatUser");

        String Name= nameEdit.getText().toString().trim();
        String  User =  emailEdit.getText().toString().trim();
        String  Password =passwordEdit.getText().toString().trim();
        String  birthday = birthEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String  mobile = mobileEdit.getText().toString().trim();

        QBUser qbUser = new QBUser(User, Password);
        qbUser.setFullName(Name);
        qbUser.getFullName();
        qbUser.setTags(Tag_Name);
        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                dialog.dismiss();
                saveUserData(qbUser);
                Log.e("QuickBloxSuccess","Success");
                Intent i=new Intent(getApplicationContext(),Home.class);

                editor.putString("user",User);
                editor.putString("password",Password);
                editor.commit();
//                i.putExtra("User",User);
//                i.putExtra("Password", Password);
//                i.putExtra("Full_Name",Name);
//                i.putExtra("DOB", birthday);
//                i.putExtra("Phone", mobile);
//                i.putExtra("Email", email);
//                i.putExtra("Gender", gender);
                startActivity(i);
                finish();


            }


            @Override
            public void onError(QBResponseException e) {
                dialog.dismiss();
                Log.e("QuickBlox_Error",e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void buildUsersList() throws IOException {
        List<String> tags = new ArrayList<>();


//        tags.add();

        QBUsers.getUsersByTags(tags, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
//                UsersAdapter adapter = new UsersAdapter(LoginActivity.this, result);
//                userListView.setAdapter(adapter);
            }

            @Override
            public void onError(QBResponseException e) {
//                ErrorUtils.showSnackbar(userListView, R.string.login_cant_obtain_users, e,
//                        R.string.dlg_retry, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                buildUsersList();
//                            }
//                        });
            }
        });
    }

    private void registerQuickBlox()

    {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
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


}

