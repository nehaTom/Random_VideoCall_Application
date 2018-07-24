package com.example.abc.random_videocall_application;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class list_user_activity extends AppCompatActivity {

    ListView lstuser;
    Button btn_create_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_activity);
        lstuser = findViewById(R.id.lstuser);
        lstuser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btn_create_chat = findViewById(R.id.btn_create_chat);
        btn_create_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice = lstuser.getCount();
                if (lstuser.getCheckedItemPositions().size()==1)
                    createPrivateChat(lstuser.getCheckedItemPositions());
                else if (lstuser.getCheckedItemPositions().size()>1)
                    createGroupChatt(lstuser.getCheckedItemPositions());
                else
                    Toast.makeText(list_user_activity.this,"Please select friend to chat",Toast.LENGTH_LONG);
            }
        });
        retrieveAllUser();

    }

    private void createGroupChatt(SparseBooleanArray checkedItemPositions)
    {
        final ProgressDialog dialog=new ProgressDialog(list_user_activity.this);
        dialog.setMessage("Please waiting...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        int countChoice=lstuser.getCount();
        ArrayList<Integer> occupantIdsList= new ArrayList<>();
        for (int i=0; i<countChoice;i++)
        {
            if (checkedItemPositions.get(i))
            {
                QBUser user= (QBUser) lstuser.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }

        ///Create Chat Dialog
        QBChatDialog dialog1=new QBChatDialog();
        dialog1.setName(Common.createChatDialogName(occupantIdsList));
        dialog1.setType(QBDialogType.GROUP);
        dialog1.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog1).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(),"Create chat dialog successfully",Toast.LENGTH_LONG);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
Log.e("GroupError",e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog dialog=new ProgressDialog(list_user_activity.this);
        dialog.setMessage("Please waiting...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        int countChoice=lstuser.getCount();
        ArrayList<Integer> occupantIdsList= new ArrayList<>();
        for (int i=0; i<countChoice;i++)
        {
            if (checkedItemPositions.get(i))
            {
                QBUser user= (QBUser) lstuser.getItemAtPosition(i);
               QBChatDialog qbChatDialog= DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(qbChatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(),"Create Private chat dialog successfully",Toast.LENGTH_LONG);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("GroupError",e.getMessage());
                    }
                });
            }
        }

    }

    private void retrieveAllUser() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {

            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle)
            {
                QBUsersHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutcurrent = new ArrayList<QBUser>();
                for (QBUser user : qbUsers)
                {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                    {
                        qbUserWithoutcurrent.add(user);
                    }

                    ListUsersAdapter adapter=new ListUsersAdapter(getBaseContext(),qbUserWithoutcurrent);
                    lstuser.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(QBResponseException errors)
            {
                Log.e("Error",errors.getMessage());
            }
        });
    }
}