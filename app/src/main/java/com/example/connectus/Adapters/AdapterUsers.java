package com.example.connectus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectus.ChatDetailActivity;
import com.example.connectus.Models.ModelUser;
import com.example.connectus.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends  RecyclerView.Adapter<AdapterUsers.MyHolder> {


    Context context;
    List<ModelUser>userList;
    //constructor


    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
// get data
        String hisUID=userList.get(position).getUserId();
        String userImage=userList.get(position).getImage();
        String userName=userList.get(position).getUserName();
//        String email=userList.get(position).getEmail();

        //set data
        holder.mNameTv.setText(userName);
        if (userImage != null && !userImage.trim().isEmpty()) {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.mAvatarIv);
        } else {
            Picasso.get().load(R.drawable.avatar).into(holder.mAvatarIv);
            // Load a default image or handle the case when userImage is null or empty
        }
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.mAvatarIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.mAvatarIv);
        }
        // handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*
            Click user form user list to start chatting
            start activity by putting UID of receiover
            we will use that uid to identify the user we are gonna chat
             */
                Intent intent= new Intent(context, ChatDetailActivity.class);
                intent.putExtra("hisUid",hisUID);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv,mEmailTv;
        public  MyHolder(View itemview){
            super(itemview);
            mAvatarIv=itemview.findViewById(R.id.avatarIv);
            mNameTv=itemview.findViewById(R.id.nameTv);
//            mAvatarIv=itemview.findViewById(R.id.avatarIv);

        }
    }

}
