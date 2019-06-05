package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biin.doan4.View.ChatActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class InboxAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<User> users;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private User user;
    private ViewHolder viewHolder;

    public InboxAdapter(Context context, int idLayout, List<User> users) {
        this.context = context;
        this.idLayout = idLayout;
        this.users = users;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView url;
        TextView username;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(idLayout, null);
            viewHolder.url = (ImageView) convertView.findViewById(R.id.inbox_img);
            viewHolder.username = (TextView) convertView.findViewById(R.id.inbox_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        user = users.get(position);
        viewHolder.username.setText(user.getUser_fullname());
        if (user.getUser_avatar().equals("")) {
            viewHolder.url.setImageResource(R.drawable.userimg);
        } else {
            Picasso.get().load(user.getUser_avatar()).resize(600, 600).into(viewHolder.url);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Client User", (Serializable) users.get(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
