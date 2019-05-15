package com.example.biin.doan4;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.biin.doan4.Adapter.ChatAdapter;
import com.example.biin.doan4.model.User;
import com.example.biin.doan4.model.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private ListView lvChat;
    private ImageView ivBack, ivClient, ivCamera, ivSend;
    private TextView tvClient;
    private EditText edtMess;

    private User client, me;
    private ChatAdapter adapter = null;
    private ArrayList<UserChat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();
        getName();

        client = (User) getIntent().getSerializableExtra("Client User");
        Picasso.get().load(client.getUser_avatar()).transform(new CircleTransform()).resize(40, 40).into(ivClient);
        tvClient.setText(client.getUser_fullname());
        //updateUI();

        adapter = new ChatAdapter(this, R.layout.item_chat, chats);
        lvChat.setAdapter(adapter);

        loadData();

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMess.getText().toString();
                UserChat a = new UserChat(mess, "", mAuth.getCurrentUser().getUid(), client.getUser_id(), me.getUser_fullname());
                mData.child("Chats").child(mAuth.getCurrentUser().getUid()).child(client.getUser_id()).push().setValue(a);
                mData.child("Chats").child(client.getUser_id()).child(mAuth.getCurrentUser().getUid()).push().setValue(a, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        edtMess.setText("");
                    }
                });
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        lvChat = findViewById(R.id.lvChat);
        ivBack = findViewById(R.id.chat_back);
        ivClient = findViewById(R.id.chat_userimg);
        ivCamera = findViewById(R.id.chat_camera);
        ivSend = findViewById(R.id.chat_send);
        tvClient = findViewById(R.id.chat_client);
        edtMess = findViewById(R.id.chat_mess);
        chats = new ArrayList<>();
        //tvClient.setText();
    }

    private void updateUI() {
        Picasso.get().load(client.getUser_avatar()).resize(40, 40).into(ivClient);
        tvClient.setText(client.getUser_fullname());
    }

    private void loadData() {
        mData.child("Chats").child(mAuth.getCurrentUser().getUid()).child(client.getUser_id()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserChat chat = dataSnapshot.getValue(UserChat.class);
                chats.add(chat);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getName() {
        mData.child("User").orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dn : dataSnapshot.getChildren()) {
                    me = dn.getValue(User.class);
//                    UserChat a = new UserChat("Bắt đầu", client.getUser_avatar(), mAuth.getCurrentUser().getUid(), me.getUser_fullname());
//                    mData.child("Chats").child(mAuth.getCurrentUser().getUid()).child(client.getUser_id()).push().setValue(a);
//                    mData.child("Chats").child(client.getUser_id()).child(mAuth.getCurrentUser().getUid()).push().setValue(a);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
