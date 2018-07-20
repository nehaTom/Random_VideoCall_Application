package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

class ExistingUser_Card_Adapter extends RecyclerView.Adapter<ExistingUser_Card_Adapter.MyViewHolder>{

    private final Context context;
    private final int[] images;
    private final String fromWhere;

    public ExistingUser_Card_Adapter(Context context,String fromWhere, int[] images)
    {
        this.context=context;
        this.fromWhere=fromWhere;
        this.images=images;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView name, User_chitchat, History_chitchat,History_time;
        ImageView User_video,User_call,User_chat,User_fav;



        public MyViewHolder(View itemView) {
            super(itemView);



            profile_image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
            User_chitchat = itemView.findViewById(R.id.User_chitchat);
            History_chitchat = itemView.findViewById(R.id.History_chitchat);
            User_video = itemView.findViewById(R.id.User_video);
            User_call = itemView.findViewById(R.id.User_call);
            User_chat = itemView.findViewById(R.id.User_chat);

            User_fav = itemView.findViewById(R.id.User_fav);
//            User_fav.setTag("blank");
            User_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tagValve=User_fav.getTag().toString();
                    if (tagValve.equals("filled")) {
                        User_fav.setBackgroundResource(R.drawable.heart);
                        User_fav.setTag("blank");
                        //isFavourite = true;
                        //saveState(isFavourite);

                    } else {
                        User_fav.setBackgroundResource(R.drawable.heartfill);
                        User_fav.setTag("filled");
                        //isFavourite = false;
                        //saveState(isFavourite);

                    }}
            });

//

            History_time = itemView.findViewById(R.id.History_time);


            if (fromWhere.equals("Existing_User")) {
                profile_image.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                User_chitchat.setVisibility(View.VISIBLE);
                User_video.setVisibility(View.VISIBLE);
                User_chat.setVisibility(View.VISIBLE);
                User_call.setVisibility(View.VISIBLE);
                User_fav.setVisibility(View.VISIBLE);

                History_chitchat.setVisibility(View.GONE);
                History_time.setVisibility(View.GONE);

            } else if (fromWhere.equals("Call_History")) {
                profile_image.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                History_chitchat.setVisibility(View.VISIBLE);
                History_time.setVisibility(View.VISIBLE);

                User_fav.setVisibility(View.GONE);
                User_chitchat.setVisibility(View.GONE);
                User_call.setVisibility(View.GONE);
                User_chat.setVisibility(View.GONE);
                User_video.setVisibility(View.GONE);
            } else if (fromWhere.equals("My_Contact"))
            {
                profile_image.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                User_chitchat.setVisibility(View.VISIBLE);
                User_video.setVisibility(View.VISIBLE);
                User_chat.setVisibility(View.VISIBLE);
                User_call.setVisibility(View.VISIBLE);

                User_fav.setVisibility(View.GONE);
                History_chitchat.setVisibility(View.GONE);
                History_time.setVisibility(View.GONE);
            }


        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view;
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      view = inflater.inflate(R.layout.existing_joined_card, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int i)
    {
        holder.profile_image.setImageResource(images[i]);

    }



    @Override
    public int getItemCount()
    {

        return images.length;
    }



}

