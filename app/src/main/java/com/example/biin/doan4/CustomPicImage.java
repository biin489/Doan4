package com.example.biin.doan4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomPicImage extends Dialog {
    public Activity c;
    public Dialog d;
    public TextView cam, lib;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private int REQUEST_CODE_IMAGE = 1;
    private int PICK_IMAGE_REQUEST = 71;


    public CustomPicImage(Activity a) {
        super(a);
        this.c = a;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customdialog_picimg);
        cam = findViewById(R.id.picImg_cam);
        lib = findViewById(R.id.picImg_lib);
        //event
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                c.startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                c.startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
            }
        });
    }
}
