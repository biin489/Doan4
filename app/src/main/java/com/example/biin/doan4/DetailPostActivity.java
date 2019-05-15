package com.example.biin.doan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.Adapter.CustomGridAdapter;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailPostActivity extends AppCompatActivity {

    private Post post;

    private GridView gridViewdt;
    private ImageView ivBack, ivImg, ivUser;
    private TextView tvTitle, tvPrice, tvUser, tvType, tvTt, tvBh, tvCpn, tvDetail, tvtopTitle, xtSeen, tvNum;
    private Button btnBuy, btnChat;
    private boolean kt = false;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private User user;
    private CustomGridAdapter dt = null;
    private List<String> kl_dt;
    private ArrayList<Post> listpostdt;
    NumberFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        post = (Post) getIntent().getSerializableExtra("MA");

        init();
        getData();
        setData();

        dt = new CustomGridAdapter(this, R.layout.grid_item_layout, listpostdt);
        gridViewdt.setAdapter(dt);

        //event

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    if (mAuth.getCurrentUser().getUid().equals(post.getPost_user_id())) {
                        Toast.makeText(DetailPostActivity.this, "Bạn không thể thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                    } else {
                        checkInfo(2);
                    }
                } else {
                    showLoginDialog("Bạn phải đăng nhập để thực hiện chức năng này");
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    if (mAuth.getCurrentUser().getUid().equals(post.getPost_user_id())) {
                        Toast.makeText(DetailPostActivity.this, "Bạn không thể thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                    } else {
                        checkInfo(1);
                    }
                } else {
                    showLoginDialog("Bạn phải đăng nhập để thực hiện chức năng này");
                }
//                Intent intent = new Intent(DetailPostActivity.this, OrderActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("post", (Serializable) post);
//                DetailPostActivity.this.startActivity(intent);
            }
        });

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPostActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("User", (Serializable) user);
                DetailPostActivity.this.startActivity(intent);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        xtSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPostActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", post.getPost_type());
                DetailPostActivity.this.startActivity(intent);
            }
        });
    }

    private void init() {
        tvtopTitle = findViewById(R.id.detail_topTitle);
        ivBack = findViewById(R.id.detail_back);
        ivImg = findViewById(R.id.detail_img);
        ivUser = findViewById(R.id.detail_imguser);
        tvTitle = findViewById(R.id.detail_title);
        tvPrice = findViewById(R.id.detail_price);
        tvUser = findViewById(R.id.detail_user);
        tvType = findViewById(R.id.detail_type);
        tvTt = findViewById(R.id.detail_tt);
        tvBh = findViewById(R.id.detail_bh);
        tvCpn = findViewById(R.id.detail_company);
        tvDetail = findViewById(R.id.detail_detail);
        btnBuy = findViewById(R.id.detail_btn_buy);
        btnChat = findViewById(R.id.detail_btn_chat);
        kl_dt = new ArrayList<String>();
        gridViewdt = findViewById(R.id.gr_seen);
        listpostdt = new ArrayList<>();
        xtSeen = findViewById(R.id.gr_xemthem_seen);
        tvNum = findViewById(R.id.detail_num);
    }

    private void setData() {
        tvtopTitle.setText(post.getPost_title());
        Picasso.get().load(post.getPost_image()).into(ivImg);
        tvTitle.setText(post.getPost_title());
        tvPrice.setText(formatter.format(post.getPost_price()) + " đ");
        mData.child("User").orderByChild("user_id").equalTo(post.getPost_user_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    break;
                }
                mData.child("Posts").orderByChild("post_user_id").equalTo(post.getPost_user_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvNum.setText(String.valueOf(dataSnapshot.getChildrenCount())+ " bài đăng");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                tvUser.setText(user.getUser_fullname());
                Picasso.get().load(user.getUser_avatar()).transform(new CircleTransform()).resize(50, 50).into(ivUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tvType.setText(post.getPost_type());
        tvTt.setText(post.getPost_status() + " %");
        if (post.getPost_is_guarantee()) {
            tvBh.setText("Còn bảo hành");
        } else {
            tvBh.setText("Hết bảo hành");
        }
        tvCpn.setText(post.getPost_maker());
        tvDetail.setText(post.getPost_detail());
    }

    private void checkInfo(final int option) {
        if (option == 1) {
            if (mAuth.getCurrentUser() != null) {
                mData.child("User").orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            user = dataSnapshot1.getValue(User.class);
                            if (user.getUser_phone().equals("") || user.getUser_address().equals("")) {
                                showAlertDialog();
                            } else {
                                Intent intent = new Intent(DetailPostActivity.this, OrderActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("post", (Serializable) post);
                                DetailPostActivity.this.startActivity(intent);
                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                showLoginDialog("Bạn phải đăng nhập để thực hiện chức năng này");
            }
        }
        if (option == 2) {
            mData.child("User").orderByKey().equalTo(post.getPost_user_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        user = dataSnapshot1.getValue(User.class);
                        Intent intent = new Intent(DetailPostActivity.this, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Client User", (Serializable) user);
                        DetailPostActivity.this.startActivity(intent);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn hãy cập nhật hồ sơ để có thể mua món đồ này");
        builder.setCancelable(false);
        builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cập nhật ngay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent myIntent = new Intent(DetailPostActivity.this, InfoActivity.class);
                DetailPostActivity.this.startActivity(myIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getData() {
        mData.child("Posts").orderByChild("post_type").equalTo(post.getPost_type()).limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                kl_dt.add(dataSnapshot.getKey());
                listpostdt.add(p1);
                dt.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_dt.indexOf(dataSnapshot.getKey());
                listpostdt.remove(index);
                kl_dt.remove(index);
                dt.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                Intent myIntent = new Intent(DetailPostActivity.this, LoginActivity.class);
                DetailPostActivity.this.startActivity(myIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    private String formatPrice(String price) {
//        String format = "";
//        for (int i = price.length(); i >= 0; i--) {
//            if (i != price.length()) {
//                if (((i + 1) % 3) == 0) {
//                    format += price.charAt(i) + ",";
//                } else {
//                    format += price.charAt(i);
//                }
//            } else {
//                format += price.charAt(i);
//            }
//        }
//        return format;
//    }
}
