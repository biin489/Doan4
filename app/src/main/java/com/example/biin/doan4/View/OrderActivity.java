package com.example.biin.doan4.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.PurchaseOrder;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OrderActivity extends AppCompatActivity {

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private Post post;
    private User user;
    private PurchaseOrder order;
    private CustomDialog dialog;

    private ImageView ivBack, orderImg;
    private TextView tvTitle, tvPrice, tvTT, tvCancer;
    private EditText edtName, edtPhone, edtAddress, edtNote;
    private Button btnCf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();

        post = (Post) getIntent().getSerializableExtra("post");

        getcrUser();

        //event
        btnCf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String orderid = mData.child("Orders").push().getKey();
                order = new PurchaseOrder(orderid, post.getPost_id(),post.getPost_image(),post.getPost_title(),post.getPost_user_id(),user.getUser_id(),edtAddress.getText().toString(),edtNote.getText().toString(),0);
                mData.child("Orders").child(orderid).setValue(order, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            hideProduct();
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tvCancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void init(){
        ivBack = findViewById(R.id.order_back);
        orderImg = findViewById(R.id.order_img);
        tvTitle = findViewById(R.id.order_title);
        tvPrice = findViewById(R.id.order_price);
        tvTT = findViewById(R.id.order_tt);
        tvCancer = findViewById(R.id.order_cancer);
        edtName = findViewById(R.id.order_name);
        edtPhone = findViewById(R.id.order_phone);
        edtAddress = findViewById(R.id.order_address);
        edtNote = findViewById(R.id.order_note);
        btnCf = findViewById(R.id.order_ok);
        dialog = new CustomDialog(OrderActivity.this);
    }

    private void updateUI(){
        Picasso.get().load(post.getPost_image()).into(orderImg);
        tvTitle.setText(post.getPost_title());
        tvPrice.setText("Giá: " + post.getPost_price() + "vnđ");
        tvTT.setText("Độ mới: " + post.getPost_status() + "%");
        edtName.setText(user.getUser_fullname());
        edtAddress.setText(user.getUser_address());
        edtPhone.setText(user.getUser_phone());
    }

    private void getcrUser() {
        mData.child("User").orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    user = dataSnapshot1.getValue(User.class);
                    Log.d("Biin user", user.toString());
                    updateUI();
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hideProduct() {
        post.setPost_trangthai(1);
        mData.child("Posts").child(post.getPost_id()).removeValue();
        mData.child("HidePosts").child(post.getPost_id()).setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Intent myIntent = new Intent(OrderActivity.this, MainActivity.class);
                dialog.dismiss();
                OrderActivity.this.startActivity(myIntent);
                finish();
            }
        });
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn có muốn hủy đơn hàng này?");
        builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

}
