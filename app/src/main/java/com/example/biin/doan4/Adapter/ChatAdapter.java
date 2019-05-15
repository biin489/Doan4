package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.CircleTransform;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.example.biin.doan4.model.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ChatAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<UserChat> userChatArrayAdapter;
    private UserChat chat;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    public ChatAdapter(Context context, int idLayout, List<UserChat> userChatArrayAdapter) {
        this.context = context;
        this.idLayout = idLayout;
        this.userChatArrayAdapter = userChatArrayAdapter;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return userChatArrayAdapter.size();
    }

    @Override
    public Object getItem(int position) {
        return userChatArrayAdapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView userClient;
        TextView content;
        TextView userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder = new ViewHolder();
        chat = userChatArrayAdapter.get(position);
        if (chat.getIdSender().equals(mAuth.getCurrentUser().getUid())) {
            convertView = inflater.inflate(R.layout.item_mymess, null);
            viewHolder.content = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(viewHolder);
            viewHolder.content.setText(chat.getChatContent());
        } else {
            convertView = inflater.inflate(R.layout.item_clientmess, null);
            viewHolder.content = (TextView) convertView.findViewById(R.id.message_body);
            viewHolder.userClient = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.content.setText(chat.getChatContent());
            viewHolder.userName.setText(chat.getNameSender());
            mData.child("User").orderByKey().equalTo(chat.getIdSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        Picasso.get().load(user.getUser_avatar()).transform(new CircleTransform()).resize(600, 600).into(viewHolder.userClient);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return convertView;
    }
}
