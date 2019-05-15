package com.example.biin.doan4;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.biin.doan4.Adapter.InboxAdapter;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private ListView lvInbox;
    private ImageView ivBack;
    private TextView test;

    private ArrayList<User> users;
    private ArrayList<String> lsId;
    private InboxAdapter userAdapter = null;
    private int i;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();
        loadData();

        userAdapter = new InboxAdapter(this, R.layout.item_inbox, users);
        lvInbox.setAdapter(userAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        lvInbox = findViewById(R.id.inbox_lvchat);
        ivBack = findViewById(R.id.inbox_back);
        users = new ArrayList<>();
        lsId = new ArrayList<>();
        //test = findViewById(R.id.test);
    }

    private void loadData() {
        mData.child("Chats").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {
                final String url = dataSnapshot.getKey();
                mData.child("User").orderByKey().equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn : dataSnapshot.getChildren()) {
                            user = dn.getValue(User.class);
                            lsId.add(user.getUser_id());
                            users.add(user);
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int index = lsId.indexOf(dataSnapshot.getKey());
                user = users.get(index);
                users.remove(index);
                lsId.remove(index);
                users.add(0, user);
                lsId.add(0, user.getUser_id());
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

    private void checkIfHasMess() {
        mData.child("Chats").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("Biin", "co tin nhan");
                    loadData();
                } else {
                    Log.d("Biin", "khong co tin nhan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
