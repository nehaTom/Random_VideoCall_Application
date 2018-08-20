package com.example.abc.random_videocall_application;


import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

//public class ListUsersAdapter extends BaseAdapter
//{
//    private Context context;
//    private ArrayList<QBUser> qbUserArrayList;
//
//
//    public ListUsersAdapter(Context context, ArrayList<QBUser> qbUserArrayList) {
//        this.context=context;
//        this.qbUserArrayList = qbUserArrayList;
//    }
//
//
//    @Override
//    public int getCount() {
//        return qbUserArrayList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return qbUserArrayList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            view = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);
////            TextView textView = view.findViewById(android.R.id.text1);
////            textView.setText(qbUserArrayList.get(position).getFullName());
//
//
//
//
//
//            view = inflater.inflate(R.layout.list_card, null);
//            TextView name = view.findViewById(R.id.name);
//
//            name.setText(qbUserArrayList.get(position).getFullName());
//
//           // ----------------
//             ImageView imv1=(ImageView)view.findViewById(R.id.User_chat1);
//             imv1.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View v) {
//                     Toast.makeText(context, "This is my Toast message! imv1",
//                             Toast.LENGTH_LONG).show();
////                     Intent intent=new Intent(context,ChatMessage.class);
////                     context.startActivity(intent);
//                                      }
//             });
//            ImageView imv2=(ImageView)view.findViewById(R.id.User_chat2);
//            imv2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "This is my Toast message! imv2",
//                            Toast.LENGTH_LONG).show();
//
//                }
//            });
//            ImageView imv3=(ImageView)view.findViewById(R.id.User_chat3);
//            imv3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Toast.makeText(context, "This is my Toast message! imv3",
////                            Toast.LENGTH_LONG).show();
//                    list_user_activity list_user=new list_user_activity();
//                    list_user.onClickChatIcon(getItem(position));
//                    }
//            });
//            //----------------------------------------------------------------------
//        }
//        return view;
//    }
//
//
//
//}


