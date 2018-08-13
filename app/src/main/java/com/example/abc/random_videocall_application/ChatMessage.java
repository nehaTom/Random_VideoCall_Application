package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhargavms.dotloader.DotLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;

public class ChatMessage extends AppCompatActivity {

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton submitButton;
    EditText editContent;
    ChatMessageAdapter adapter;
    DotLoader dotLoader;
    TextView userName;
    ImageView BackArrow;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //variables for del message
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get Index Contex Menu click

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        contextMenuIndexClicked = info.position;
        switch (item.getItemId()) {
//            case R.id.chat_message_update_message:
//                updateMessage();
//                break;

            case R.id.chat_message_delete_message:
                deleteMessage();
                break;

        }
        return true;
    }

    private void updateMessage() {
        //set Message for edit text
        editMessage = QBChatMessagesHolder.getInstance().getChatMessgesByDilogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        editContent.setText(editMessage.getBody());
        isEditMode = true;

    }

    private void deleteMessage() {
        ProgressDialog deleteDialog = new ProgressDialog(ChatMessage.this);
        deleteDialog.setMessage("Plese wait....");
        deleteDialog.show();
        editMessage = QBChatMessagesHolder.getInstance().getChatMessgesByDilogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                retrieveAllMessages();
                deleteDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.cat_message_contex_menu, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initView();
        initChatDialogs();
        retrieveAllMessages();
        setUserName();

        BackArrow=findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackArrow();
            }
        });



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editContent.getText().toString().isEmpty()) {
                   //
                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(editContent.getText().toString());
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);

                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        //// put messages to cache
                        QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessgesByDilogId(qbChatDialog.getDialogId());
                        adapter = new ChatMessageAdapter(getBaseContext(), messages);
                        lstChatMessages.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        //setUserName();

                        ///// Removetext from edit text
                        editContent.setText(" ");
                        editContent.setFocusable(true);
                    } else {
                        final ProgressDialog updateDialog = new ProgressDialog(ChatMessage.this);
                        updateDialog.setMessage("Plese wait....");
                        updateDialog.show();
                        QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                        messageUpdateBuilder.updateText(editContent.getText().toString()).markDelivered().markRead();

                        QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                //// Refresh data
                                retrieveAllMessages();
                                isEditMode = false;
                                updateDialog.dismiss();

                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
          //  }

        });
    }

    private void setBackArrow()
    {
        Intent intent=getIntent();
        String Activity_Name =intent.getStringExtra("Activity_Name");
        if(Activity_Name.equals("Chat_Dialog"))
        {
            intent =new Intent(this,ChatDialogsActivity.class);
            startActivity(intent);
        }
        else if (Activity_Name.equals("List_User"))
        {
            intent =new Intent(this,list_user_activity.class);
            startActivity(intent);
        }


    }

    private void setUserName()
    {
        userName=findViewById(R.id.userName);
        String SenderName=  sharedPreferences.getString("SenderName","");
        userName.setText(SenderName);
    }


    private void initView() {
        dotLoader=findViewById(R.id.dot_loader);
        lstChatMessages = findViewById(R.id.list_of_messages);
        submitButton = findViewById(R.id.send_button);
        editContent = findViewById(R.id.edit_content);
        editContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    qbChatDialog.sendIsTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {


            }

            @Override
            public void afterTextChanged(Editable s)
            {
                try {
                    qbChatDialog.sendStopTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }
        });
        // dotLoader=findViewById()

        ////Add contex menu

        registerForContextMenu(lstChatMessages);


//       TextView BackArrow=findViewById(R.id.BackArrow);
//        BackArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(),ChatDialogsActivity.class);
//                startActivity(intent);
//            }
//        });
//        ImageButton attachment_button=findViewById(R.id.attachment_button);
//        attachment_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                attachImage();
//            }
//        });

    }


    private void initChatDialogs() {
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
//        if (qbChatDialog.getPhoto()!= null && ! qbChatDialog.getPhoto().equals("null"))
//        {
//            QBContent.getFile(Integer.parseInt(qbChatDialog.getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
//                @Override
//                public void onSuccess(QBFile qbFile, Bundle bundle) {
//
//                }
//
//                @Override
//                public void onError(QBResponseException e) {
//
//                }
//            });
//
//        }
        qbChatDialog.initForChat(QBChatService.getInstance());


        ////////////// Register Listener Incoming Message
        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                //// cache message
                QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessgesByDilogId(qbChatMessage.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(), messages);
                lstChatMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

                Log.e("Error", e.getMessage());

            }
        });

        ///Add typing listener

        reisterTypingForChatDialog(qbChatDialog);


    }

    private void reisterTypingForChatDialog(QBChatDialog qbChatDialog) {
        QBChatDialogTypingListener typingListener= new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer integer)
            {
                if (dotLoader.getVisibility() != View.VISIBLE)
                    dotLoader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void processUserStopTyping(String dialogId, Integer integer) {

                if (dotLoader.getVisibility() != View.INVISIBLE)
                    dotLoader.setVisibility(View.INVISIBLE);
            }
        };
        qbChatDialog.addIsTypingListener(typingListener);


    }



    private void retrieveAllMessages()
    {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if (qbChatDialog != null)
        {
            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
return;
    }
}
