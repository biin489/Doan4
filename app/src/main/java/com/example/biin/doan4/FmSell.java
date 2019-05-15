package com.example.biin.doan4;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.biin.doan4.Adapter.PfPostAdapter;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FmSell extends Fragment {

    ListView lv;
    View view;

    private String Userid;
    private User user;
    private ArrayList<Post> posts;
    private ArrayList<String> lsId;
    private PfPostAdapter adapter = null;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    public FmSell() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fm_sell,container,false);

        Userid = getArguments().getString("Userid");
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();

        adapter = new PfPostAdapter(getContext(),R.layout.item_chat, posts, 0);
        lv.setAdapter(adapter);

        return view;
    }

    private void init() {
        lv = view.findViewById(R.id.fs_lv);
        posts = new ArrayList<>();
        lsId = new ArrayList<>();
        getData();
    }

    private void getData() {
        mData.child("User").orderByChild("user_id").equalTo(Userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    user = ds.getValue(User.class);
                    break;
                }
                mData.child("Posts").orderByChild("post_user_id").equalTo(user.getUser_id()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post temp = dataSnapshot.getValue(Post.class);
                        lsId.add(dataSnapshot.getKey());
                        posts.add(temp);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post temp = dataSnapshot.getValue(Post.class);
                        int index = lsId.indexOf(dataSnapshot.getKey());
                        posts.remove(index);
                        posts.add(index,temp);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Post temp = dataSnapshot.getValue(Post.class);
                        int index = lsId.indexOf(dataSnapshot.getKey());
                        lsId.remove(index);
                        posts.remove(index);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
