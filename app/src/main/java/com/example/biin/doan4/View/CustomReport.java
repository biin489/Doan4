package com.example.biin.doan4.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.Report;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.util.Calendar;

public class CustomReport extends Dialog {
    public Activity c;
    public Dialog d;
    private ImageButton ibImage;
    private EditText edtContent;
    private TextView tvOk;
    private String user;
    private Bitmap bitmap;
    private int REQUEST_CODE_IMAGE = 1;
    private String url;

    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    StorageReference storageRef;

    public CustomReport(final Activity a, String user) {
        super(a);
        this.c = a;
        this.user = user;
        storageRef = FirebaseStorage.getInstance().getReference();
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_report);
        super.onCreate(savedInstanceState);
        ibImage = findViewById(R.id.rp_img);
        edtContent = findViewById(R.id.rp_content);
        tvOk = findViewById(R.id.rp_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtContent.getText().length() == 0) {
                    Toast.makeText(c, "Bạn phải nhập lý do báo cáo", Toast.LENGTH_SHORT).show();
                } else {
                    if (bitmap != null) {
                        try {
                            Toast.makeText(c, "Đang tải...", Toast.LENGTH_SHORT).show();
                            UploadImgFromCamera();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(c, "Bạn phải thêm ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ibImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                c.startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void UploadImgFromCamera() throws InterruptedException {
// Get the data from an ImageView as bytes
        Calendar calendar = Calendar.getInstance();
        url = "postImg/image" + calendar.getTimeInMillis() + ".jpg";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.child(url).putBytes(data);
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
                return storageRef.child(url).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri download = task.getResult();
                    String postId = mData.child("Reports").push().getKey();
                    Report report = new Report(postId, edtContent.getText().toString(), user, mAuth.getCurrentUser().getUid(), download.toString(), 1);
                    mData.child("Reports").child(postId).setValue(report);
                    Toast.makeText(c, "Báo cáo người dùng thành công, chúng tôi sẽ không tiết lộ danh tính của bạn", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {

                }
            }
        });
    }
}
