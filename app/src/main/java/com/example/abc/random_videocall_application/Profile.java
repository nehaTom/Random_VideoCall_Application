package com.example.abc.random_videocall_application;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBBaseCustomObject;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
TextView id,age,gmail,gender,submit;
    String Interested_In,images ;
    CircleImageView imv;
    private static int RESULT_LOAD_IMAGE = 1;
EditText name,phone,state,height,weight,Ethnicity,aboutYou;
RadioGroup interestedRadioGroup;
RadioButton male,female;
ProgressDialog dialog;
String Name, Email,Mobile,Password,Birthday,Gender;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        imv = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.imageview);
        createSessionForChat();

        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        gmail = findViewById(R.id.gmail);
        gender = findViewById(R.id.gender);
        submit = findViewById(R.id.submit);

        age = findViewById(R.id.age);
        state = findViewById(R.id.state);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        Ethnicity = findViewById(R.id.Ethnicity);
        aboutYou = findViewById(R.id.aboutYou);
        interestedRadioGroup = findViewById(R.id.interestedRadioGroup);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        RadioGroup genderGroup = findViewById(R.id.interestedRadioGroup);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.female:
                        Interested_In = "Female";
                        break;
                    case R.id.male:
                        Interested_In = "Male";
                        break;

                }
            }
        });

        ///// Get values from Regertation


        String Name = sharedPreferences.getString("Full_Name", "");
        String Email = sharedPreferences.getString("Email", "");
        String Mobile = sharedPreferences.getString("Phone", "");
        String Password = sharedPreferences.getString("password", "");
        String Birthday = sharedPreferences.getString("Birthday", "");
        String Gender = sharedPreferences.getString("Gender", "");

name.setText(Name);
gmail.setText(Email);
phone.setText(Mobile);
gender.setText(Gender);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sumbitData();
            }
        });

        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void createSessionForChat()
    {
        String user,password,facebook_User;
        user=sharedPreferences.getString("user","");
        password=sharedPreferences.getString("password","");
        facebook_User=sharedPreferences.getString("Facebook","");

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

//                            mDialog.dismiss();
//
//                            QBSystemMessagesManager qbSystemMessagesManager=QBChatService.getInstance().getSystemMessagesManager();
//                            qbSystemMessagesManager.addSystemMessageListener(Profile.this);
//
//                            QBIncomingMessagesManager qbIncomingMessagesManager= QBChatService.getInstance().getIncomingMessagesManager();
//                            qbIncomingMessagesManager.addDialogMessageListener(list_user_activity.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void sumbitData()
    {
        String Name_Profile = name.getText().toString().trim();
        String Mobile_Profile = phone.getText().toString().trim();
        String State_Profile = state.getText().toString().trim();
        String Height_Profile = height.getText().toString().trim();
        String Weight_Profile = weight.getText().toString().trim();
        String ethinicity_Profile = Ethnicity.getText().toString().trim();
        String About_You_Profile = aboutYou.getText().toString().trim();

        String Gender = sharedPreferences.getString("Gender", "");
        //// put fields
        QBCustomObject object= new QBCustomObject();
        object.putString("Full_Name",Name_Profile);
        object.putInteger("Phone", Integer.parseInt(Mobile_Profile));
        object.putString("State",State_Profile);
//        object.putFloat("Height", Float.parseFloat(Height_Profile));
        object.putFloat("Height", 12232);
        object.putFloat("Weight", 5336);
        object.putString("Nationality",ethinicity_Profile);
        object.putString("About_Me",About_You_Profile);
        object.putString("Gender",Gender);
        object.putString("Interested_In",Interested_In);
        object.putFile("Image",images);

        object.putInteger("Age",24);
        object.putString("Parent ID",Email);

        /////////

        object.setClassName("Profile");
        QBCustomObjects.createObject(object).performAsync(new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle)
            {
                QBUser qbUser = new QBUser(Email, Password);

                qbUser.setFileId(Integer.valueOf(images));
//                name.setText(Name_Profile);
//                phone.setText(Mobile_Profile);
//                gender.setText(Gender);
//                age.setText("24");
//                state.setText(State_Profile);
                Intent intent= new Intent(Profile.this,Home2.class);
                startActivity(intent);
                Log.e("Success","Success");

            }

            @Override
            public void onError(QBResponseException e)
            {
                Log.e("Error",e.getMessage());

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            cursor.close();
//            imv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//        }
            Uri selectedImage = data.getData();
            try {
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String images = cursor.getString(columnIndex);
                //  imv.setImageURI(selectedImage);
                //      Toast.makeText(getApplicationContext(), "This is my Toast message!"+,
                //           Toast.LENGTH_LONG).show();
                Log.d("Check",images);
                // imv.setImageBitmap(null);
                imv.setImageURI(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }






    @Override
    public void onBackPressed() {
        return;
    }
}



