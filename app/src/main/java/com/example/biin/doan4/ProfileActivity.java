package com.example.biin.doan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biin.doan4.Adapter.ProfileAdapter;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView ivImg, ivBack;
    private TextView tvName, tvAge, tvGender;
    private Button btnChat;

    ProfileAdapter adapter = null;
    private User user;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        user = (User) getIntent().getSerializableExtra("User");
        init();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        tabLayout = findViewById(R.id.pf_tab);
        viewPager = findViewById(R.id.pf_vp);
        ivImg = findViewById(R.id.profile_img);
        tvName = findViewById(R.id.profile_name);
        tvAge = findViewById(R.id.profile_age);
        tvGender = findViewById(R.id.profile_gender);
        btnChat = findViewById(R.id.profile_chat);
        ivBack = findViewById(R.id.pf_back);

        updateUI();

        adapter = new ProfileAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("Userid", user.getUser_id());
        FmSell fmSell = new FmSell();
        fmSell.setArguments(bundle);
        adapter.addFragment(fmSell, "Đang bán");

        Bundle bundle2 = new Bundle();
        bundle2.putString("Userid", user.getUser_id());
        FmSelled fmSelled = new FmSelled();
        fmSelled.setArguments(bundle2);
        adapter.addFragment(fmSelled, "Đã bán");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void updateUI() {
        if (user.getUser_avatar().equals("")) {

        } else {
            Picasso.get().load(user.getUser_avatar()).transform(new CircleTransform()).resize(150, 150).into(ivImg);
        }
        if (user.getUser_fullname().equals("")) {
            tvName.setText(user.getUser_name());
        } else {
            tvName.setText(user.getUser_fullname());
        }
        if (user.getUser_age() == 0) {
            tvAge.setText("Tuổi chưa rõ");
        } else {
            tvAge.setText(String.valueOf(user.getUser_age()) + " tuổi");
        }
        if (user.getUser_gender().equals("")) {
            tvGender.setText("Giới tính chưa rõ");
        } else {
            tvGender.setText("Giới tính: " + user.getUser_gender());
        }
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().getUid().equals(user.getUser_id())) {
                btnChat.setVisibility(View.GONE);
                //btnChat.setBackgroundColor(0xFF9E9E9E);
            } else {
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Client User", (Serializable) user);
                        ProfileActivity.this.startActivity(intent);
                    }
                });
            }
        } else {
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginDialog("Bạn cần đăng nhập để thực hiện chức năng này");
                }
            });
        }
    }

    private void showLoginDialog(String noti) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage(noti);
        builder.setCancelable(true);
        builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
