package com.example.abc.random_videocall_application;

import android.os.Bundle;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBChatMessagesHolder {
    private static QBChatMessagesHolder instance;
    private Bundle bundle;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessagesHolder getInstance(){
        QBChatMessagesHolder qbChatMessagesHolder;
        synchronized (QBChatMessagesHolder.class)
        {
            if(instance== null)
                instance = new QBChatMessagesHolder();
            qbChatMessagesHolder=instance;
        }
        return qbChatMessagesHolder;
    }

    private QBChatMessagesHolder()
    {
        this.qbChatMessageArray=new HashMap<>();
        bundle=new Bundle();
    }
    private  void setBundle(Bundle bundle)
    {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
public int getUnreadMessageByDialogId(String id)
{
    return this.bundle.getInt(id);
}


    public void putMessage(String dialogId, ArrayList<QBChatMessage> qbChatMessages)
    {
        this.qbChatMessageArray.put(dialogId,qbChatMessages);
    }
    public void putMessage(String dialogId,QBChatMessage  qbChatMessage)
    {
        List<QBChatMessage> lstResult=(List)this.qbChatMessageArray.get(dialogId);
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lstAdded =new ArrayList(lstResult.size());
        lstAdded.addAll(lstResult);
        putMessage(dialogId,lstAdded);

    }

    public ArrayList<QBChatMessage> getChatMessgesByDilogId(String dialogId)
    {
        return (ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogId);
    }
}
