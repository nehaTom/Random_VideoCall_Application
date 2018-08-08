package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.IdentityHashMap;

public class ChatMessageAdapter extends BaseAdapter {

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
        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.d("myTag", " " + qbChatMessages.get(i).getSenderId());
            Log.d("myTag", " " + QBChatService.getInstance().getUser().getId());

            if (qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())){

                view = inflater.inflate(R.layout.list_send_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(i).getBody());


            } else {

                sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                view = inflater.inflate(R.layout.list_rec_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(i).getBody());
                TextView txtName = (TextView)view.findViewById(R.id.message_user);
                txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName());
                String SenderName= QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName();
                //editor.putString("SenderName",SenderName);
                //editor.commit();
            }
        }
        return view;
    }
}
