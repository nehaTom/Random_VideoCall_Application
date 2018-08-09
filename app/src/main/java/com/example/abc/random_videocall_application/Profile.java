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

public class Profile extends AppCompatActivity {
TextView id,age,gmail,gender,submit;
ImageView upload_photo;
EditText name,phone,state,height,weight,Ethnicity,aboutYou;
RadioGroup interestedRadioGroup;
RadioButton male,female;
ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        id = findViewById(R.id.id);
        phone = findViewById(R.id.phone);
        name  = findViewById(R.id.name);
        gmail = findViewById(R.id.gmail);
        gender = findViewById(R.id.gender);
        submit=findViewById(R.id.submit);
        upload_photo  = findViewById(R.id.upload_photo);

        age = findViewById(R.id.age);
        state = findViewById(R.id.state);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        Ethnicity  = findViewById(R.id.Ethnicity);
        aboutYou = findViewById(R.id.aboutYou);
        interestedRadioGroup = findViewById(R.id.interestedRadioGroup);

        male = findViewById(R.id.male);
        female  = findViewById(R.id.female);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfile();
            }
        });



        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Intent intent=new Intent();
intent.setType("image/*");
intent.setAction(Intent.ACTION_GET_CONTENT);
startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.SELECT_PICTURE);

                //setProfileImage();
            }
        });

setData();
        //ageCalculate();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Common.SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
//                dialog.setMessage("Please wait...");
//                dialog.setCancelable(false);
//                dialog.show();

                ////update user avtar
                try {
                    InputStream image = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(image);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();

                    /// Get File size
                    final int imageSizeKB = (int) file.length() / 1024;
                    if (imageSizeKB >= (1024 * 100)) {
                        Toast.makeText(this, "Error image size", Toast.LENGTH_SHORT).show();
                    }

                    ///// upload file to server
                    QBContent.uploadFileTask(file,true,null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                           /// set avtar for user
                            QBUser user = new QBUser();
                            user.setId(QBChatService.getInstance().getUser().getId());
                            user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                            // /update user

                            QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {

                                    dialog.dismiss();
                                    upload_photo.setImageBitmap(bitmap);
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


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setData()
    {


//
//        switch (Gender.toString()) {
//            case "F":
//                Gender = "Female";
//                break;
//            case "M":
//                Gender = "Male";
//                break;
//            case "O":
//                Gender = "Other";
//                break;
//
//        }


//        id.setText("ID:"+User_Id);
//        phone.setText(Phone);
//        gmail.setText(Email);
//        name.setText(User_Name);
////        gender.setText(Gender);
//
//        state.setText(State);
//        height.setText(Height+"Ft.");
//        weight.setText(Weight+"Kg.");
//
//        Ethnicity.setText(ethinicity);
//        aboutYou.setText(About_You);
    }
    /*private String ageCalculate()
    {

         DOB = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;

    }*/

    private void setProfile()
    {

        String State = state.getText().toString().trim();
        String Height = height.getText().toString().trim();
        String Weight = weight.getText().toString().trim();
        String ethinicity = Ethnicity.getText().toString().trim();
        String About_You = aboutYou.getText().toString().trim();

        Intent intent=getIntent();
        String User=intent.getStringExtra("User");
        String Password=intent.getStringExtra("Password");
        String DOB=intent.getStringExtra("DOB");
        String Phone=intent.getStringExtra("Phone");
        String Email=intent.getStringExtra("Email");
        String User_Name=intent.getStringExtra("Full_Name");
        String Gender=intent.getStringExtra("Gender");


//// put fields
        QBCustomObject object= new QBCustomObject();
        object.putString("Gender","");
        object.putString("Age","");
        object.putString("State",State);
        object.putString("Height",Height);
        object.putString("Weight",Weight);
        object.putString("Ethnicity",ethinicity);
        object.putString("About_You",About_You);
        object.putString("Interested_In","");

       ///// set the class name

       object.setClassName("User_Profile");

        QBCustomObjects.createObject(object).performAsync(new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle) {


                QBUser qbUser = new QBUser(User, Password);
               // qbUser.setId(QBChatService.getInstance().getUser().getId());
               Integer ID= qbUser.getId();
                id.setText("ID:"+ID);
        phone.setText(Phone);
        gmail.setText(Email);
        name.setText(User_Name);
        gender.setText(Gender);

        state.setText(State);
        height.setText(Height+"Ft.");
        weight.setText(Weight+"Kg.");

        Ethnicity.setText((CharSequence) Ethnicity);
        aboutYou.setText(About_You);
                Intent intent= new Intent(Profile.this,Home.class);
                startActivity(intent);

            }
            @Override
            public void onError(QBResponseException e) {
Log.e("Error_Profile",e.getMessage());
            }
        });







    }
    private void setProfileUpdate()

    {
        Intent intent=getIntent();
        String User=intent.getStringExtra("User");
        String Password=intent.getStringExtra("Password");
        QBUser qbUser = new QBUser(User, Password);
        qbUser.setId(QBChatService.getInstance().getUser().getId());
        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toast.makeText(Profile.this,"User:"+qbUser.getFullName()+"Updated",Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }


    private void loadUserProfile()

    {
        //// Load avtar
        QBUsers.getUser(QBChatService.getInstance().getUser().getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                ///save to cache

                QBUsersHolder.getInstance().putUsers(qbUser);
                if (qbUser.getFileId() != null) {
                    int profilePictureId = qbUser.getFileId();

                    QBContent.getFile(profilePictureId).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            String fileUrl=qbFile.getPublicUrl();
                            Picasso.get().load(fileUrl).into(upload_photo);
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                }
            }
            @Override
            public void onError(QBResponseException e) {

            }
        });
        QBUser currentUser =QBChatService.getInstance().getUser();
        String fullName=currentUser.getFullName();
        String email=currentUser.getLogin();
        String phone=currentUser.getPhone();
        //// then set there


    }


    @Override
    public void onBackPressed() {

        return;
    }




}
