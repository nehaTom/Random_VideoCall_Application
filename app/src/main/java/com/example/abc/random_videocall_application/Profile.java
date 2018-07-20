package com.example.abc.random_videocall_application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;

public class Profile extends AppCompatActivity {
TextView id,age,gmail,gender;
CircularImageView profile_image;
EditText name,phone,state,height,weight,Ethnicity,aboutYou;
RadioGroup interestedRadioGroup;
RadioButton male,female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        id = findViewById(R.id.id);
        phone = findViewById(R.id.phone);
        name  = findViewById(R.id.name);
        gmail = findViewById(R.id.gmail);
        gender = findViewById(R.id.gender);


        profile_image  = findViewById(R.id.profile_image);
        age = findViewById(R.id.age);
        state = findViewById(R.id.state);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        Ethnicity  = findViewById(R.id.Ethnicity);
        aboutYou = findViewById(R.id.aboutYou);
        interestedRadioGroup = findViewById(R.id.interestedRadioGroup);

        male = findViewById(R.id.male);
        female  = findViewById(R.id.female);



        setData();
        //ageCalculate();

    }



    private void setData()
    {

       String State = state.getText().toString().trim();
       String Height = height.getText().toString().trim();
       String Weight = weight.getText().toString().trim();
       String ethinicity = Ethnicity.getText().toString().trim();
       String About_You = aboutYou.getText().toString().trim();


        Intent intent=getIntent();
        String DOB=intent.getStringExtra("DOB");
        String Phone=intent.getStringExtra("Phone");
        String Email=intent.getStringExtra("Email");
        String User_Id=intent.getStringExtra("UeserId");
        String User_Name=intent.getStringExtra("Username");
        String Gender=intent.getStringExtra("Gender");

        switch (Gender) {
            case "F":
                Gender = "Female";
                break;
            case "M":
                Gender = "Male";
                break;
            case "O":
                Gender = "Other";
                break;

        }


        id.setText("ID:"+User_Id);
        phone.setText(Phone);
        gmail.setText(Email);
        name.setText(User_Name);
        gender.setText(Gender);

        state.setText(State);
        height.setText(Height+"Ft.");
        weight.setText(Weight+"Kg.");

        Ethnicity.setText(ethinicity);
        aboutYou.setText(About_You);
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

}
