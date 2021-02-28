package com.example.puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    private Context context;
    private ArrayList<Profile> userList;
    private String userEmail, userName, userScore, userImage;

    public AdapterUsers(Context context, ArrayList<Profile> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        userImage = userList.get(position).getImageUrl();
        userEmail = "Email : " + userList.get(position).getEmail();
        userName = "Name : " + userList.get(position).getFirst_Name() + " " + userList.get(position).getLast_Name();
        userScore = "Score : " + userList.get(position).getScore();

        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        holder.mScoreTv.setText(userScore);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img1_foreground).into(holder.mavatarIv);
        } catch (Exception e) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("ID", userList.get(position).getUid());
                editor.commit();
                context.startActivity(new Intent(context, ActivitySpecificPersonPuzzles.class));

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {


        ImageView mavatarIv;
        TextView mNameTv, mScoreTv, mEmailTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mavatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.personNameTv);
            mScoreTv = itemView.findViewById(R.id.scoreTv);
            mEmailTv = itemView.findViewById(R.id.personEmailTv);

        }

    }

}
