package com.example.abc.random_videocall_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ListUsersAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<QBUser> qbUserArrayList;

    public ListUsersAdapter(Context context, ArrayList<QBUser> qbUserArrayList) {
        this.context=context;
        this.qbUserArrayList = qbUserArrayList;
    }


    @Override
    public int getCount() {
        return qbUserArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);
//            TextView textView = view.findViewById(android.R.id.text1);
//            textView.setText(qbUserArrayList.get(position).getFullName());





            view = inflater.inflate(R.layout.list_card, null);
            TextView name = view.findViewById(R.id.name);

            name.setText(qbUserArrayList.get(position).getFullName());


        }
        return view;
    }

}


