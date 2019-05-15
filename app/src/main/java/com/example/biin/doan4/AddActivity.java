package com.example.biin.doan4;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.biin.doan4.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    StorageReference mountainsRef;
    StorageReference storageRef;
    DatabaseReference mData;

    private ImageView imgvCheck, imgvGetImg;
    private EditText edtTitle, edtMaker, edtDetail, editPrice, editStatus;
    private CheckBox chbGuarantee;
    private CardView cvImg;
    private Spinner spOption;

    private String[] Option = {"Đồ điện tử", "Đồ gia dụng", "Thời trang", "Đồ văn phòng", "Khác"};
    private int REQUEST_CODE_IMAGE = 1;
    private String uriImg;
    private String postId;
    private Uri download;
    private boolean isHasUploadimg;
    private Post post;
    private boolean isHasImage, isDone;
    private String Type;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private CustomPicImage picImage;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();

        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imgvGetImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                picImage.show();
            }
        });

        imgvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitle.getText().toString().equals("") || edtMaker.getText().toString().equals("") || edtDetail.getText().toString().equals("") || editPrice.getText().toString().equals("") || editStatus.getText().toString().equals("") || Type == "") {
                    Toast.makeText(AddActivity.this, "Bạn phải nhập đủ các trường", Toast.LENGTH_SHORT).show();
                } else {
                    if (isHasImage) {
                        dialog.show();
                        try {
                            UploadImgFromCamera();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(AddActivity.this, "Hãy chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Type = "Đồ điện tử";
                        break;
                    case 1:
                        Type = "Đồ gia dụng";
                        break;
                    case 2:
                        Type = "Thời trang";
                        break;
                    case 3:
                        Type = "Đồ văn phòng";
                        break;
                    case 4:
                        Type = "Khác";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Type = "";
            }
        });
    }

    private void init() {
        dialog = new CustomDialog(AddActivity.this);
        imgvCheck = findViewById(R.id.add_btn_add);
        imgvGetImg = findViewById(R.id.add_imgs);
        edtTitle = findViewById(R.id.add_title);
        edtMaker = findViewById(R.id.add_maker);
        edtDetail = findViewById(R.id.add_detail);
        editPrice = findViewById(R.id.add_price);
        editStatus = findViewById(R.id.add_status);
        chbGuarantee = findViewById(R.id.add_check_guarantee);
        cvImg = findViewById(R.id.add_cvimg);
        spOption = findViewById(R.id.add_spOption);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddActivity.this, android.R.layout.simple_spinner_item, Option);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOption.setAdapter(arrayAdapter);
        picImage = new CustomPicImage(AddActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        picImage.dismiss();
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgvGetImg.setImageBitmap(bitmap);
            imgvGetImg.setScaleX(1);
            imgvGetImg.setScaleY(1);
            imgvGetImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            isHasImage = true;
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgvGetImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 666, 500, false));
                imgvGetImg.setScaleX(1);
                imgvGetImg.setScaleY(1);
                imgvGetImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                isHasImage = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadImgFromCamera() throws InterruptedException {
// Get the data from an ImageView as bytes
        Calendar calendar = Calendar.getInstance();
        String url = "postImg/image" + calendar.getTimeInMillis() + ".jpg";
        mountainsRef = storageRef.child(url);
        imgvGetImg.setDrawingCacheEnabled(true);
        imgvGetImg.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgvGetImg.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return mountainsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    download = task.getResult();
                    postId = mData.child("Posts").push().getKey();
                    post = new Post(postId, mAuth.getCurrentUser().getUid(), edtTitle.getText().toString(), edtDetail.getText().toString(), Long.parseLong(editPrice.getText().toString()), download.toString(), Type, Integer.parseInt(editStatus.getText().toString()), edtMaker.getText().toString(), chbGuarantee.isChecked(), 1, 0);
                    mData.child("Posts").child(postId).setValue(post);
                    Toast.makeText(AddActivity.this,"Đăng thành công!",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                } else {

                }
            }
        });
    }
}
