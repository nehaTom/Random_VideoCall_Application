package com.example.abc.random_videocall_application;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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


    }

    @Override
    public void onBackPressed() {
        return;
    }
}



