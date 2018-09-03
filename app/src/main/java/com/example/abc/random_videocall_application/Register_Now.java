package com.example.abc.random_videocall_application;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_Now extends AppCompatActivity {
    RadioButton maleGender, femaleGender, otherGender;
    RadioGroup genderGroup;
    EditText nameEdit, mobileEdit, passwordEdit, birthEdit, emailEdit;
    Button registrationBtn;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;
    SharedPreferences.Editor editor;
    Map<String, String> header;
    String name, mobile, password, Birthday, gender, email, ageS;
    boolean doubleBackToExitPressedOnce = false;


    static final String APP_ID = "72405";
    static final String AUTH_KEY = "zCNmPJGEkrGyseU";
    static final String AUTH_SECRET = "V6nrN7Cdv2Vt2Vm";
    static final String ACCOUNT_KEY = "qAx_5ERjtk6Fy_tBh1rs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__now);

        Calendar myCalendar = Calendar.getInstance();
        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        maleGender = findViewById(R.id.maleGender);
        femaleGender = findViewById(R.id.femaleGender);
        otherGender = findViewById(R.id.otherGender);

        nameEdit = findViewById(R.id.nameEdit);
        mobileEdit = findViewById(R.id.mobileEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        birthEdit = findViewById(R.id.birthEdit);
        emailEdit = findViewById(R.id.emailEdit);

        initializeQuickBlox();
        registerQuickBlox();


        genderGroup = findViewById(R.id.genderRadioGroup);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.femaleGender:
                        gender = "Female";
                        break;
                    case R.id.maleGender:
                        gender = "Male";
                        break;
                    case R.id.otherGender:
                        gender = "Other";
                        break;

                }
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
                if (today.get(Calendar.DAY_OF_YEAR) < myCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                Integer ageInt = new Integer(age);
                ageS = ageInt.toString();
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                birthEdit.setText(sdf.format(myCalendar.getTime()));
            }

        };
        birthEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(Register_Now.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });


        registrationBtn = findViewById(R.id.registrationBtn);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    quickBloxValidation();

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
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();


    }


    private boolean checkValidation() {
        String email = emailEdit.getText().toString();

        if (nameEdit.getText().toString().equals("")) {
            nameEdit.setError("field Cannot be empty");
            return false;
        } else if (emailEdit.getText().toString().equals("")) {
            emailEdit.setError("field Cannot be empty");
            return false;
        } else if (emailValidator(email) == false) {
            emailEdit.setError("Enter correct email");
            return false;
        } else if (mobileEdit.getText().toString().equals("")) {
            mobileEdit.setError("field Cannot be empty");
            return false;
        } else if (mobileEdit.getText().toString().length() < 10) {
            mobileEdit.setError("mobile No cannot be less than 10 number");
            return false;
        } else if (passwordEdit.getText().toString().equals("")) {
            passwordEdit.setError("field Cannot be empty");
            return false;
        } else if (birthEdit.getText().toString().equals("")) {
            birthEdit.setError("field Cannot be empty");
            return false;
        } else if (genderGroup.getCheckedRadioButtonId() == -1) {
            showMessage("Error", "Please select gender");

            return false;
        }
        return true;
    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void quickBloxValidation()

    {
        dialog.show();
        StringifyArrayList<String> Tag_Name = new StringifyArrayList<String>();
        Tag_Name.add("chatUser");

        String Name = nameEdit.getText().toString().trim();
        String User = emailEdit.getText().toString().trim();
        String Password = passwordEdit.getText().toString().trim();
        String Birthday = birthEdit.getText().toString();
        String email = emailEdit.getText().toString().trim();
        String mobile = mobileEdit.getText().toString().trim();

        QBUser qbUser = new QBUser(User, Password);
        qbUser.setFullName(Name);
        qbUser.getFullName();
        qbUser.setTags(Tag_Name);
        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                saveUserData(qbUser);
                Log.e("QuickBloxSuccess", "Success");

                editor.putString("Full_Name", Name);
                editor.putString("Gender", gender);
                editor.putString("user", User);
                editor.putString("password", Password);
                editor.putString("Birthday", ageS);
                editor.putString("Email", email);
                editor.putString("Phone", mobile);
                editor.putString("App_User", "Simple_Login");
                editor.putBoolean("hasLoggedIn", true);
                editor.putString("ID", qbUser.getId().toString());
                editor.commit();
                qbUser.setPassword(Password);
                SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                getProfileById(qbUser.getId().toString());


            }


            @Override
            public void onError(QBResponseException e) {
                dialog.dismiss();
                Log.e("QuickBlox_Error", e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getProfileById(String id) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.eq("userId", id);
        Performer<ArrayList<QBCustomObject>> performer = QBCustomObjects.getObjects("Profile", requestBuilder);
        performer.performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if (qbCustomObjects.size() > 0) {
                    String id = qbCustomObjects.get(0).getCustomObjectId();
                    HashMap<String, Object> fields = qbCustomObjects.get(0).getFields();
                    Log.e("Check", fields.get("Interested_In").toString());
                    Log.e("Check", fields.get("Gender").toString());
                    editor.putString("Interested_In", fields.get("Interested_In").toString());
                    editor.putString("Profile_Id", qbCustomObjects.get(0).getCustomObjectId());
                    editor.putString("PName", fields.get("Full_Name").toString());
                    editor.commit();

                }

                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("FromWhere", "Register");
                startActivity(i);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("TAG", "checking");
                editor.putString("Interested_In", "");
                editor.putString("Profile_Id", "");
                editor.putString("PName", "");
                editor.commit();
                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("FromWhere", "Register");
                startActivity(i);
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
                Log.e("Error", e.getMessage());
            }
        });
    }

    private void initializeQuickBlox() {
        //
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);

        QBSettings.getInstance().setEndpoints("https://api.quickblox.com", "chat.quickblox.com", ServiceZone.PRODUCTION);
        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Register_Now.this, New_Login.class);
        startActivity(intent);
        finish();
    }
//

}

