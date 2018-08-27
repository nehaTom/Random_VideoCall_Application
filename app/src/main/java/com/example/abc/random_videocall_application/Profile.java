package com.example.abc.random_videocall_application;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
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
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBBaseCustomObject;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.QBCustomObjectsFiles;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.customobjects.model.QBCustomObjectFileField;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    TextView id, age, gmail, gender, submit;
    String Interested_In, images;
    CircleImageView imv;
    private static int RESULT_LOAD_IMAGE = 1;
    EditText name, phone, state, height, weight, Ethnicity, aboutYou;
    RadioGroup interestedRadioGroup;
    RadioButton male, female;
    ProgressDialog dialog;
    String Name, Email, Mobile, Password, Birthday, Gender;
    Uri photoToUpload;
    Date date;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    private String Name_Profile,Mobile_Profile ,
    State_Profile ,
    Height_Profile ,
    Weight_Profile ,
    ethinicity_Profile,About_You_Profile;

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
        date =Calendar.getInstance().getTime();

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


         Name = sharedPreferences.getString("Full_Name", "");
         Email = sharedPreferences.getString("Email", "");
         Mobile = sharedPreferences.getString("Phone", "");
         Password = sharedPreferences.getString("password", "");
         Birthday = sharedPreferences.getString("Birthday", "");
         Gender = sharedPreferences.getString("Gender", "");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/mm/yy");

        try {
            date = new Date();
            date =simpleDateFormat.parse(Birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String Gender = sharedPreferences.getString("Gender", "");
        String dateValue = getAge(date.getDay(),date.getMonth(),date.getYear())+"";
        age.setText(dateValue);
        name.setText(Name);
        gmail.setText(Email);
        phone.setText(Mobile);
        gender.setText(Gender);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    setVaribales();
                    if (getIntent().getStringExtra("FromWhere").equals("Profile")) {
                        updateProfile();
                    } else {
                        sumbitData();
                    }
                }

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
        setDataIfExist();
        checkPermissions();
    }

    private void updateProfile() {
        QBCustomObject record = new QBCustomObject();
        record.setClassName("Profile");
        HashMap<String, Object> object = new HashMap<String, Object>();
        object.put("Full_Name", Name_Profile);
        if(!Mobile_Profile.isEmpty()) {
            object.put("Phone", Integer.parseInt(Mobile_Profile));
        }
        object.put("State", State_Profile);
//        object.putFloat("Height", Float.parseFloat(Height_Profile));
        object.put("Height", 12232);
        object.put("Weight", 5336);
        object.put("Nationality", ethinicity_Profile);
        object.put("About_Me", About_You_Profile);
        object.put("Gender", Gender);
        object.put("Interested_In", Interested_In);
        record.setFields(object);
        record.setCustomObjectId(sharedPreferences.getString("Profile_Id",""));
        Performer<QBCustomObject> performer =  QBCustomObjects.updateObject(record);
        showProgressDialog();
        performer.performAsync(new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle) {
                if(photoToUpload == null){
                    hideProgressDialog();
                    Intent i = new Intent(getApplicationContext(), Home2.class);
                    startActivity(i);
                }else {
                    upload_check();
                }

            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
            }
        });

    }

    private void checkPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("yes","yes");

                } else {
                    Log.d("yes","no");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void createSessionForChat() {
        String user, password, facebook_User;
        user = sharedPreferences.getString("user", "");
        password = sharedPreferences.getString("password", "");
        facebook_User = sharedPreferences.getString("Facebook", "");

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


        final QBUser qbUser = new QBUser(user, password);
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
    public void setVaribales(){
         Name_Profile = name.getText().toString().trim();
         Mobile_Profile = phone.getText().toString().trim();
         State_Profile = state.getText().toString().trim();
         Height_Profile = height.getText().toString().trim();
         Weight_Profile = weight.getText().toString().trim();
         ethinicity_Profile = Ethnicity.getText().toString().trim();
         About_You_Profile = aboutYou.getText().toString().trim();
    }

    private void sumbitData() {
        showProgressDialog();


         Gender = sharedPreferences.getString("Gender", "");
        //// put fields
        QBCustomObject object = new QBCustomObject();
        object.putString("Full_Name", Name_Profile);
        object.putInteger("Phone", Integer.parseInt(Mobile_Profile));
        object.putString("State", State_Profile);
//        object.putFloat("Height", Float.parseFloat(Height_Profile));
        object.putFloat("Height", 12232);
        object.putFloat("Weight", 5336);
        object.putString("Nationality", ethinicity_Profile);
        object.putString("About_Me", About_You_Profile);
        object.putString("Gender", Gender);
        object.putString("Interested_In", Interested_In);
        object.putFile("Image", images);

        object.putInteger("Age", 24);
        object.putString("Parent ID", Email);

        /////////

        object.setClassName("Profile");
        QBCustomObjects.createObject(object).performAsync(new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle)
            {
                if(photoToUpload == null){
                     hideProgressDialog();
                    Intent i = new Intent(getApplicationContext(), Home2.class);
                    startActivity(i);
                }else {
                    upload_check();
                }

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error3", e.getMessage());
                hideProgressDialog();

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
            Bitmap bmp;

            try {
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String images = cursor.getString(columnIndex);
                //  imv.setImageURI(selectedImage);
                //      Toast.makeText(getApplicationContext(), "This is my Toast message!"+,
                //           Toast.LENGTH_LONG).show();
                Log.d("Check", images);
                // imv.setImageBitmap(null);
                bmp=getScaledBitmap(selectedImage);
                imv.setImageBitmap(bmp);

                Log.e("Image",bmp.toString());
                //imv.setImageURI(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

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
    public void upload_check(){
        File file = new File(photoToUpload.getPath());
        String Profile_Id = sharedPreferences.getString("Profile_Id","");
        QBCustomObject customObject = new QBCustomObject();
        customObject.setClassName("Profile");
        //int id =0;
        //id = Integer.parseInt(Profile_Id);
        customObject.setCustomObjectId(Profile_Id);

        QBCustomObjectsFiles.uploadFile(file, customObject, "Image", new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {

            }
        }).performAsync(new QBEntityCallback<QBCustomObjectFileField>() {
            @Override
            public void onSuccess(QBCustomObjectFileField uploadFileResult, Bundle params) {
                Log.e("Check1","success");
                Toast.makeText(getApplicationContext(), "Profile Updated",
                        Toast.LENGTH_LONG).show();
                hideProgressDialog();
                Intent i = new Intent(getApplicationContext(), Home2.class);
                startActivity(i);
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("Check2",errors.getMessage());
                hideProgressDialog();
            }
        });
    }


    private Bitmap getScaledBitmap(Uri selectedImage) {

        Bitmap thumb = null;
        try {
            photoToUpload = selectedImage;
            ContentResolver cr = getApplicationContext().getContentResolver();
            InputStream in = cr.openInputStream(selectedImage);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            thumb = BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
        }
        return thumb;
    }

    private void setDataIfExist(){
        name.setText(sharedPreferences.getString("Full_Name",""));
        //editor.putString("INTERESTEDIN",fields.get("Interested_In").toString());
        if(sharedPreferences.getString("INTERESTEDIN","").equalsIgnoreCase("male")){
            male.setSelected(true);
            female.setSelected(false);
        }else{
            male.setSelected(false);
            female.setSelected(true);
        }
        aboutYou.setText(sharedPreferences.getString("About_Me",""));
        phone.setText(sharedPreferences.getString("Phone",""));
        state.setText(sharedPreferences.getString("State",""));
        height.setText(sharedPreferences.getString("Height",""));
        gender.setText(sharedPreferences.getString("Gender",""));
        age.setText(sharedPreferences.getString("Age",""));
        Ethnicity.setText(sharedPreferences.getString("Nationality",""));
        weight.setText(sharedPreferences.getString("Weight",""));
        gmail.setText(sharedPreferences.getString("user",""));

    }

    private boolean checkValidation() {


        if (phone.getText().toString().equals("")) {
            phone.setError("field Cannot be empty");
            return false;
        } else if (name.getText().toString().equals("")) {
            name.setError("field Cannot be empty");
            return false;
        }
        else if (state.getText().toString().equals("")) {
            state.setError("field Cannot be empty");
            return false;
        } else if (height.getText().toString().equals("")) {
            height.setError("field Cannot be empty");
            return false;
        } else if (weight.getText().toString().equals("")) {
            weight.setError("field Cannot be empty");
            return false;
        } else if (Ethnicity.getText().toString().equals("")) {
            Ethnicity.setError("field Cannot be empty");
            return false;
        } else if (gender.getText().toString().equals("")) {
            gender.setError("field Cannot be empty");
            return false;
        }
        else if (interestedRadioGroup.getCheckedRadioButtonId() == -1) {
            showMessage("Error","Please select gender");
            return false;
        }
        return true;
    }
    public void showMessage(String title, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplication());
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.set
        builder.setMessage(message);
        //builder.show();
        android.support.v7.app.AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((android.support.v7.app.AlertDialog) dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.white);
            }
        });
        dialog1.show();

    }
    private int getAge(int day, int month, int year){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(day, month,year );

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        // String ageS = ageInt.toString();

        return ageInt;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}



