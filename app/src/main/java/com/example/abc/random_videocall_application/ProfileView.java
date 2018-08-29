package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.QBCustomObjectsFiles;
import com.quickblox.customobjects.model.QBCustomObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileView extends AppCompatActivity {

    TextView id, age, gmail, gender, submit,interestedIn, name, phone, state, height, weight, Ethnicity, aboutYou;
    CircleImageView imv;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String  profileId;
    private ProgressDialog progressDialog;
    ImageView BackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initWidgets();
        getProfileById();
        setOnClickEditProfile();
        onClckBack();
    }

    private void onClckBack() {
        BackArrow = findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home2.class);
                startActivity(i);

            }
        });
    }

    private void setOnClickEditProfile() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("FromWhere","Profile");
                startActivity(i);
            }
        });

    }

    void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
            progressDialog.setOnKeyListener(keyListener);
        }

        progressDialog.setMessage("Loading Profile");

        progressDialog.show();

    }
    void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void getProfileById() {
        showProgressDialog();
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.eq("userId", profileId);
        Performer<ArrayList<QBCustomObject>> performer =  QBCustomObjects.getObjects("Profile",requestBuilder);
        performer.performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if(qbCustomObjects.size()>0) {
                    for(int i = 0 ;i <qbCustomObjects.size();i++) {
                        if(qbCustomObjects.get(0).getUserId().equals(profileId)) {
                            String id = qbCustomObjects.get(0).getCustomObjectId();
                            HashMap<String, Object> fields = qbCustomObjects.get(0).getFields();
                            Log.e("Check", fields.get("Interested_In").toString());
                            Log.e("Check", fields.get("Gender").toString());
                            name.setText(fields.get("Full_Name").toString());
                            editor.putString("NAME", fields.get("Full_Name").toString());
                            interestedIn.setText(fields.get("Interested_In").toString());
                            editor.putString("INTERESTEDIN", fields.get("Interested_In").toString());
                            aboutYou.setText(fields.get("About_Me").toString());
                            editor.putString("About_Me", fields.get("About_Me").toString());
                            phone.setText(fields.get("Phone").toString());
                            editor.putString("Phone", fields.get("Phone").toString());
                            state.setText(fields.get("State").toString());
                            editor.putString("State", fields.get("State").toString());
                            height.setText(fields.get("Height").toString());
                            editor.putString("Height", fields.get("Height").toString());
                            gender.setText(fields.get("Gender").toString());
                            editor.putString("Gender", fields.get("Gender").toString());
                            age.setText(fields.get("Age").toString());
                            editor.putString("Birthday", fields.get("Age").toString());
                            Ethnicity.setText(fields.get("Nationality").toString());
                            editor.putString("Nationality", fields.get("Nationality").toString());
                            weight.setText(fields.get("Weight").toString());
                            editor.putString("Weight", fields.get("Weight").toString());
                            editor.commit();
                            gmail.setText(sharedPreferences.getString("user", ""));
                            //hideProgressDialog();
                            setImageIfexist();
                        }
                    }

                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("TAG","checking");
                hideProgressDialog();
            }
        });

    }

    public void setImageIfexist(){
        QBCustomObject customObject = new QBCustomObject("Profile", profileId);
        Performer<InputStream> performer = QBCustomObjectsFiles.downloadFile(customObject,"Image");
        performer.performAsync(new QBEntityCallback<InputStream>() {
            @Override
            public void onSuccess(InputStream inputStream, Bundle bundle) {
                if(inputStream != null) {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
                    imv.setImageBitmap(bitmap1);
                }
                hideProgressDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
            }
        });

    }

    private void initWidgets() {

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
        interestedIn = findViewById(R.id.interestedIn);
        profileId = sharedPreferences.getString("ID","");
        Log.e("check",profileId);

    }
}
