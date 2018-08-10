package com.example.abc.random_videocall_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class NewJoined_GridView extends BaseAdapter {

    private Context mContext;
    private ArrayList<QBUser> qbUserWithoutCurrent;

    public NewJoined_GridView(Context context, ArrayList<QBUser> qbUserWithoutCurrent) {
        mContext = context;
        this.qbUserWithoutCurrent = qbUserWithoutCurrent;
    }

    @Override
    public int getCount() {
        return qbUserWithoutCurrent.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserWithoutCurrent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.new_joined_grid_layout, null);
            TextView name = (TextView) gridViewAndroid.findViewById(R.id.name);
            CircleImageView profile_image = (CircleImageView) gridViewAndroid.findViewById(R.id.profile_image);
            String[] names = new String[qbUserWithoutCurrent.size()];
//            for( i = 0;i<qbUserWithoutCurrent.size();i++) {
                QBUser qbUser = new QBUser();
                qbUser = qbUserWithoutCurrent.get(i);
                names[i] = qbUser.getFullName();
                name.setText(names[i]);

                //uploadphoto.setImageDrawable(roundedBitmapDrawable);
            profile_image.setImageResource(R.drawable.profile);
        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}
