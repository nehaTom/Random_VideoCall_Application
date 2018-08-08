package com.example.abc.random_videocall_application;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;

public class Call_History extends AppCompatActivity implements QBSystemMessageListener,QBChatDialogMessageListener {

    RecyclerView CallHistory_RecyclerView;
    LinearLayoutManager layoutManager;
    ExistingUser_Card_Adapter adapter;

    ImageView home, newUser, existingUser, chatList, contact,home_white, newUser_white, existingUser_white,
            chatList_white, contact_white;
    int[] images = {
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call__history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRecyclerView();
        setData();
        setOnClicks();
        createSessionForChat();
        loadChatDialogs();



        /////////////////////

        CallHistory_RecyclerView=findViewById(R.id.CallHistory_RecyclerView);
//        CallHistory_RecyclerView.setOnClickListener(new AdapterView.OnItemClickListener() {
//                                                        @Override
//                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                                            QBChatDialog qbChatDialog = (QBChatDialog) CallHistory_RecyclerView.getAdapter().getItemId(position);
//                                                            Intent intent=new Intent(Call_History.this,ChatMessage.class);
//
//                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
//                startActivity(intent);
////
//                                                        }
//                                                    });

//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                QBChatDialog qbChatDialog= (QBChatDialog) CallHistory_RecyclerView.getAdapter().getItemId(position);
//                Intent intent=new Intent(Call_History.this,ChatMessage.class);
//
//                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
//                startActivity(intent);
//
//            }
//        });

    }



    private void setData() {
        home = findViewById(R.id.home);
        newUser = findViewById(R.id.newUser);
        existingUser = findViewById(R.id.existingUser);
        chatList = findViewById(R.id.chatList);
        contact = findViewById(R.id.contact);

        newUser_white = findViewById(R.id.newUser_white);
        existingUser_white = findViewById(R.id.existingUser_white);
        chatList_white = findViewById(R.id.chatList_white);
        home_white = findViewById(R.id.home_white);
        contact_white = findViewById(R.id.contact_white);
    }


    private void setOnClicks() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                home.setBackgroundColor(Color.parseColor("#ffffff"));
//                home.setImageResource(R.drawable.home);
                home.setVisibility(View.GONE);
                home_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), Home.class);
                startActivity(intent);
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                newUser.setBackgroundColor(Color.parseColor("#ffffff"));
//                newUser.setImageResource(R.drawable.newlyadded);

                newUser.setVisibility(View.GONE);
                newUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), NewJoined.class);
                startActivity(intent);
            }
        });

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                existingUser.setBackgroundColor(Color.parseColor("#ffffff"));
//                existingUser.setImageResource(R.drawable.existinguser);

                existingUser.setVisibility(View.GONE);
                existingUser_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), list_user_activity.class);
                startActivity(intent);
            }
        });
        chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                chatList.setBackgroundColor(Color.parseColor("#ffffff"));
//                chatList.setImageResource(R.drawable.chat);

                chatList.setVisibility(View.GONE);
                chatList_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), ChatDialogsActivity.class);
                startActivity(intent);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                contact.setBackgroundColor(Color.parseColor("#ffffff"));
//                contact.setImageResource(R.drawable.contacts);

                contact.setVisibility(View.GONE);
                contact_white.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplication(), My_Contacts.class);
                startActivity(intent);
            }
        });
    }
    private void setRecyclerView()
    {
        CallHistory_RecyclerView=findViewById(R.id.CallHistory_RecyclerView);
        // CallHistory_RecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        CallHistory_RecyclerView.setLayoutManager(layoutManager);
        adapter = new ExistingUser_Card_Adapter(this,"Call_History",images);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        CallHistory_RecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels, "list"));
        CallHistory_RecyclerView.setAdapter(adapter);
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {

    }

    private void createSessionForChat()

    {

    }


    private void loadChatDialogs()
    {

    }


}
