package com.example.abc.random_videocall_application;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Footer extends AppCompatActivity {
    ImageView home, newUser, existingUser, chatList, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);
        setData();
        setOnClicks();

    }


    public void setData() {
        home = findViewById(R.id.home);
        newUser = findViewById(R.id.newUser);
        existingUser = findViewById(R.id.existingUser);
        chatList = findViewById(R.id.chatList);
        contact = findViewById(R.id.contact);
    }


    private void setOnClicks() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.setBackgroundColor(Color.parseColor("#ffffff"));
                home.setImageResource(R.drawable.home);
                Intent intent = new Intent(getApplication(), Home.class);
                startActivity(intent);
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser.setBackgroundColor(Color.parseColor("#ffffff"));
                newUser.setImageResource(R.drawable.newlyadded);
                Intent intent = new Intent(getApplication(), NewJoined.class);
                startActivity(intent);
            }
        });

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existingUser.setBackgroundColor(Color.parseColor("#ffffff"));
                existingUser.setImageResource(R.drawable.existinguser);
                Intent intent = new Intent(getApplication(), Existing_User.class);
                startActivity(intent);
            }
        });
        chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatList.setBackgroundColor(Color.parseColor("#ffffff"));
                chatList.setImageResource(R.drawable.chat);
                Intent intent = new Intent(getApplication(), Call_History.class);
                startActivity(intent);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setBackgroundColor(Color.parseColor("#ffffff"));
                contact.setImageResource(R.drawable.contacts);
                Intent intent = new Intent(getApplication(), My_Contacts.class);
                startActivity(intent);
            }
        });
    }
}