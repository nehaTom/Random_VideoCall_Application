package com.example.abc.random_videocall_application;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Register_Now extends AppCompatActivity {
    RadioButton maleGender,femaleGender,otherGender;
    RadioGroup genderRadioGroup;
    EditText nameEdit,mobileEdit,passwordEdit,birthEdit,emailEdit;
    Button registrationBtn;
    Map<String, String> header;
    String name, mobile, password, birthday, gender,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__now);

        maleGender=findViewById(R.id.maleGender);
        femaleGender =findViewById(R.id.femaleGender);
        otherGender =findViewById(R.id.otherGender);

        nameEdit =findViewById(R.id.nameEdit);
        mobileEdit =findViewById(R.id.mobileEdit);
        passwordEdit =findViewById(R.id.passwordEdit);
        birthEdit =findViewById(R.id.birthEdit);
        emailEdit=findViewById(R.id.emailEdit);

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

                            RegisterationApi();
                        }
                    }
                });


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


            private void RegisterationApi() {
                birthday = birthEdit.getText().toString().trim();
                name = nameEdit.getText().toString().trim();
                email = emailEdit.getText().toString().trim();
                mobile = mobileEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();

                String url = "http://192.168.1.28:8888/LoginAPI/REST/service/Registration";

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                        dialog.dismiss();
                                Log.e("my app", "123" + response);

                                if (response != null) {
                                    try {
                                        JSONArray resarray = new JSONArray(response);

                                        JSONObject resobj=null;

                                        for(int i=0; i < resarray.length(); i++ )
                                        {
                                            resobj = resarray.getJSONObject(i);

                                        }

                                        if (resobj.getString("Status").equalsIgnoreCase("Success")) {

                                            String profile = resobj.getString("Profile");

                                            JSONObject profileobj = new JSONObject(profile);

                                            String DOB = profileobj.getString("dateOfBirth");
                                            String Phone = profileobj.getString("phoneNo");
                                            String Email = profileobj.getString("emailId");
                                            String UeserId = profileobj.getString("uqId");
                                            String Username = profileobj.getString("userName");
                                            String Gender = profileobj.getString("gender");
                                            quickBloxValidation();

                                            Intent i = new Intent(Register_Now.this, Profile.class);
                                            i.putExtra("DOB", DOB);
                                            i.putExtra("Phone", Phone);
                                            i.putExtra("Email", Email);
                                            i.putExtra("UeserId", UeserId);
                                            i.putExtra("Username", Username);
                                            i.putExtra("Gender", Gender);
                                            startActivity(i);
                                        } else {
                                            showMessage("Error", "Wrong Username or password");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    showMessage("Error", "Wrong Username or password");
                                }


                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                        Log.e("my app1", "error");

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
                        params.put("phoneNo", mobile);
                        params.put("userName", name);
                        params.put("password", password);
                        params.put("gender", gender);
                        params.put("dateOfBirth", birthday);
                        return params;
                    }

                };
                requestQueue.add(jsonObjRequest);

            }
    private void quickBloxValidation()

    {
      String  User =  emailEdit.getText().toString().trim();
      String  Password =nameEdit.getText().toString().trim();
        QBUser qbUser = new QBUser(User, Password);
        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("QuickBlox_Error",e.getMessage());
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


}
