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

import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
TextView id,age,gmail,gender,submit;
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

        sumbitData();

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

    private void sumbitData()
    {
        String State_Profile = state.getText().toString().trim();
        String Height_Profile = height.getText().toString().trim();
        String Weight_Profile = weight.getText().toString().trim();
        String ethinicity_Profile = Ethnicity.getText().toString().trim();
        String About_You = aboutYou.getText().toString().trim();
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



