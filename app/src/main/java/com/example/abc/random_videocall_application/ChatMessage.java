package com.example.abc.random_videocall_application;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhargavms.dotloader.DotLoader;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
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
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatMessage extends AppCompatActivity {

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton submitButton;
    EditText editContent;
    ChatMessageAdapter adapter;
    DotLoader dotLoader;
    QBUser selectedUser;
    ImageButton attachment_button;
    TextView userName, date, lastSeen;
    ImageView BackArrow, User_call,Image_upload;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;
    private static int RESULT_LOAD_IMAGE = 1;
    Uri photoToUpload;
    Bitmap bmp;

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


//        Image_upload=findViewById(R.id.Image_upload);
        date = findViewById(R.id.date);
//        createSessionForFacebook();
        initView();
        initChatDialogs();
        retrieveAllMessages();
        setUserName();
        setLastseen();
        setImageAttachment();

        //  setDate();

        ///// set date
//        DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
//            }


        BackArrow = findViewById(R.id.BackArrow);
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
                    //////////////////////////////////////////////////
                    //chatMessage.setAttachments();
//                    long millis=(qbChatDialog.getLastMessageDateSent())/1000;
//                    long m = (millis / 60) % 60;
//                    long h = (millis / (60 * 60))%24;
//                    String hms = String.format("%02d:%02d", h,
//                            m);
//                    // String Last_Seen=qbChatDialog.getLastMessageDateSent();
//                    editor.putString("Last_Seen",hms);
//                    editor.commit();
                    chatMessage.setDateSent(Calendar.getInstance().getTime().getTime());
                    Log.e("SysT", String.valueOf(Calendar.getInstance().getTime()));
                    chatMessage.setMarkable(true);
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

    private void setImageAttachment()
    {

        attachment_button=findViewById(R.id.attachment_button);

        attachment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
               // Image_upload.setImageBitmap(bmp);

                Log.e("Image",bmp.toString());
                //imv.setImageURI(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

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
    private void setLastseen() {
        Intent intent = getIntent();
        lastSeen = findViewById(R.id.lastSeen);

        String Activity_Name = intent.getStringExtra("Activity_Name");
        if (Activity_Name.equals("Chat_Dialog")) {
            String Last_Seen = sharedPreferences.getString("Last_Seen", "");
            lastSeen.setText("last seen " + Last_Seen);
        } else if (Activity_Name.equals("List_User")) {
            String Last_Seen = sharedPreferences.getString("Last_Seen_List", "");
            lastSeen.setText("last seen " + Last_Seen);


        }
    }

    private void setDate() {
        Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }


    private void setBackArrow() {
        Intent intent = getIntent();
        String Activity_Name = intent.getStringExtra("Activity_Name");
        if (Activity_Name.equals("Chat_Dialog")) {
            intent = new Intent(this, ChatDialogsActivity.class);
            startActivity(intent);
        } else if (Activity_Name.equals("List_User")) {
            intent = new Intent(this, list_user_activity.class);
            startActivity(intent);
        }


    }

    private void setUserName() {
        userName = findViewById(R.id.userName);
        String SenderName = sharedPreferences.getString("SenderName", "");
        userName.setText(SenderName);
    }


    private void initView() {
        dotLoader = findViewById(R.id.dot_loader);

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
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
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
        QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer integer) {
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


    private void retrieveAllMessages() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext(), qbChatMessages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    //  private void createSessionForFacebook()
//    { String Provider =sharedPreferences.getString("Facebook","");
//        final QBUser qbUser=new QBUser(Provider);
//        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession qbSession, Bundle bundle) {
//                qbUser.setId(qbSession.getUserId());
//                try {
//                    qbUser.setPassword(String.valueOf(BaseService.getBaseService().getToken()));
//                } catch (BaseServiceException e) {
//                    e.printStackTrace();
//                }
//
//                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
//                    @Override
//                    public void onSuccess(Object o, Bundle bundle) {
//
//                        //mDialog.dismiss();
//
//
//                    }
//
//                    @Override
//                    public void onError(QBResponseException e) {
//                        // mDialog.dismiss();
//                    }
//                });
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
////                mDialog.dismiss();
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        return;
    }

////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////



    private class ChatMessageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<QBChatMessage> qbChatMessages;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
            this.context = context;
            this.qbChatMessages = qbChatMessages;

        }

        @Override
        public int getCount() {
            return qbChatMessages.size();
        }

        @Override
        public Object getItem(int i) {
            return qbChatMessages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                Log.d("myTag", " " + qbChatMessages.get(i).getSenderId());
                Log.d("myTag", " " + QBChatService.getInstance().getUser().getId());
                User_call=findViewById(R.id.User_call);
                User_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        QBUser selected=;
//                        selectedUser = qbChatMessages.get(i);
//                        if (isLoggedInChat()) {
//                            startCall(false);
//                        }
//                        if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
//                            startPermissionsActivity(true);
//                        }


                    }
                });

                if (qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())) {

                    view = inflater.inflate(R.layout.list_send_message, null);
                    BubbleTextView bubbleTextView = (BubbleTextView) view.findViewById(R.id.message_content);
                    TextView time = view.findViewById(R.id.time);
                    TextView date = view.findViewById(R.id.date);
                    ImageView Image_send=view.findViewById(R.id.Image_send);
                    Image_send.setImageBitmap(bmp);
                    long date_value = qbChatMessages.get(i).getDateSent();
                    String dateValue = Long.toString(date_value);
                    date.setText(dateValue);
                    bubbleTextView.setText(qbChatMessages.get(i).getBody());
                    long millis = (qbChatMessages.get(i).getDateSent()) / 1000;
                    //long s = millis % 60;
                    long m = (millis / 60) % 60;
                    long h = (millis / (60 * 60)) % 24;
                    String hms = String.format("%02d:%02d", h,
                            m);

                    time.setText(hms);
                    time.setTextColor(Color.BLACK);
                    Log.e("time", String.valueOf(qbChatMessages.get(i).getDateSent() / 1000));

                } else {

                    sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    view = inflater.inflate(R.layout.list_rec_message, null);
                    BubbleTextView bubbleTextView = (BubbleTextView) view.findViewById(R.id.message_content);
                    TextView time = view.findViewById(R.id.time);

                    TextView date = view.findViewById(R.id.date);
                    long date_value = qbChatMessages.get(i).getDateSent();
                    String dateValue = Long.toString(date_value);
                    date.setText(dateValue);
                    //date.setText((int) qbChatMessages.get(i).getDateSent());

                    bubbleTextView.setText(qbChatMessages.get(i).getBody());
                    TextView txtName = (TextView) view.findViewById(R.id.message_user);
                    txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName());
                    time.setText("" + qbChatMessages.get(i).getDateSent());
                    time.setTextColor(Color.BLACK);
                    String SenderName = QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName();

                    //editor.putString("SenderName",SenderName);
                    //editor.commit();
                }
            }
            return view;
        }
    }
}
