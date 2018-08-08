package com.example.abc.random_videocall_application;

import android.os.Bundle;

public class QBUnreadMessageHolder
{
    private static QBUnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance()
    {
        QBUnreadMessageHolder qbUnreadMessageHolder;
        synchronized (QBUnreadMessageHolder.class)
        {
            if(instance==null)
                instance = new QBUnreadMessageHolder();
            qbUnreadMessageHolder = instance;

        }
        return qbUnreadMessageHolder;
    }

private QBUnreadMessageHolder()
{
     bundle= new Bundle();
}

public void setBundle(Bundle bundle)
{
    this.bundle = bundle;
}

public Bundle getBundle()
{
    return this.bundle;
}

public int getUnreadMessageByDialogId(String id)
{
    return this.bundle.getInt(id);
}
}
