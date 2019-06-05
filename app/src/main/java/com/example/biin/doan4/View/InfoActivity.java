package com.example.biin.doan4.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.R;
import com.example.biin.doan4.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

public class InfoActivity extends AppCompatActivity {

    private TextView tvUname, tvMail, tvVerify;
    private ImageView ivBack, ivOk, ivAvatar;
    private EditText edtName, edtAress, edtPhone;
    private Button btn_age;
    private Spinner spGender;
    private Button btnAvt;

    private String[] Gender = {"Nam", "Nữ", "Khác"};
    private String gender;
    ArrayAdapter<String> arrayAdapter;
    private int PICK_IMAGE_REQUEST = 71;
    private int REQUEST_CODE_IMAGE = 1;
    private Uri filePath;
    private Uri download;
    private CustomPicImage picImage;
    private Bitmap bitmap;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    StorageReference mountainsRef;
    StorageReference storageRef;
    FirebaseStorage storage;

    private User user, updateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        getInfo();
        init();

        //event
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        gender = "Nam";
                        break;
                    case 1:
                        gender = "Nữ";
                        break;
                    case 2:
                        gender = "Khác";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "";
            }
        });

        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(InfoActivity.this,"Bạn phải nhập tên", Toast.LENGTH_SHORT).show();
                if (edtName.getText().toString().equals("")) {
                    Toast.makeText(InfoActivity.this, "Bạn phải nhập họ tên", Toast.LENGTH_SHORT).show();
                } else {
                    if (edtPhone.getText().toString().equals("")) {
                        Toast.makeText(InfoActivity.this, "Bạn phải nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    } else {
                        if (btn_age.getText().toString().equals("")) {
                            Toast.makeText(InfoActivity.this, "Bạn phải nhập tuổi", Toast.LENGTH_SHORT).show();
                        } else {
                            if (edtAress.getText().toString().equals("")) {
                                Toast.makeText(InfoActivity.this, "Bạn phải nhập địa chỉ", Toast.LENGTH_SHORT).show();
                            } else {
                                if (bitmap != null) {
                                    Toast.makeText(InfoActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                    try {
                                        UploadImgFromCamera();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    updateUser = new User(mAuth.getCurrentUser().getUid(), user.getUser_name(), edtName.getText().toString(), user.getUser_email(), edtAress.getText().toString(), edtPhone.getText().toString(), Integer.parseInt(btn_age.getText().toString()), gender, user.getUser_avatar(), user.getUser_isLinked(), user.isUser_isVerify(), user.getUser_rule());
                                    mData.child("User").child(mAuth.getCurrentUser().getUid()).setValue(updateUser);
                                    Toast.makeText(InfoActivity.this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        });

        btnAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        btn_age.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - year));
                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
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
        ivAvatar = findViewById(R.id.info_avt);
        ivBack = findViewById(R.id.info_back);
        ivOk = findViewById(R.id.info_ok);
        btn_age = findViewById(R.id.info_age);
        edtAress = findViewById(R.id.info_aress);
        edtName = findViewById(R.id.info_name);
        edtPhone = findViewById(R.id.info_phone);
        spGender = findViewById(R.id.info_gender);
        tvUname = findViewById(R.id.info_uname);
        tvMail = findViewById(R.id.info_mail);
        btnAvt = findViewById(R.id.info_btnavt);
        arrayAdapter = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item, Gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(arrayAdapter);
        picImage = new CustomPicImage(InfoActivity.this);
        tvVerify = findViewById(R.id.info_verify);
    }

    private void getInfo() {
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

    private void updateUI() {
        if (!user.getUser_avatar().equals("")) {
            Picasso.get().load(user.getUser_avatar()).into(ivAvatar);
        }
        tvUname.setText(user.getUser_name());
        if (!user.getUser_fullname().equals("")) {
            edtName.setText(user.getUser_fullname());
        }
        tvMail.setText(user.getUser_email());
        if (!user.getUser_address().equals("")) {
            edtAress.setText(user.getUser_address());
        }
        if (!user.getUser_address().equals("")) {
            edtAress.setText(user.getUser_address());
        }
        if (!user.getUser_phone().equals("")) {
            edtPhone.setText(user.getUser_phone());
        }
        if (user.getUser_age() != 0) {
            btn_age.setText(String.valueOf(user.getUser_age()));
        } else {
            btn_age.setText("");
        }
        if (!user.getUser_gender().equals("")) {
            int spinnerPosition = arrayAdapter.getPosition(user.getUser_gender());
            spGender.setSelection(spinnerPosition);
        }
        if (user.isUser_isVerify()) {
            tvVerify.setText("Đã xác minh");
            tvVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            tvVerify.setText("Xác minh");
            tvVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InfoActivity.this, VPhoneActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("User", (Serializable) user);
                    intent.putExtra("Phone", edtPhone.getText().toString());
                    InfoActivity.this.startActivity(intent);
                }
            });
        }
    }

    private void chooseImage() {
        picImage.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        picImage.dismiss();
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            ivAvatar.setImageBitmap(bitmap);
            ivAvatar.setScaleX(1);
            ivAvatar.setScaleY(1);
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivAvatar.setImageBitmap(bitmap);
                ivAvatar.setScaleX(1);
                ivAvatar.setScaleY(1);
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
        ivAvatar.setDrawingCacheEnabled(true);
        ivAvatar.buildDrawingCache();
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
                    updateUser = new User(mAuth.getCurrentUser().getUid(), user.getUser_name(), edtName.getText().toString(), user.getUser_email(), edtAress.getText().toString(), edtPhone.getText().toString(), Integer.parseInt(btn_age.getText().toString()), gender, download.toString(), user.getUser_isLinked(), user.isUser_isVerify(), user.getUser_rule());
                    mData.child("User").child(mAuth.getCurrentUser().getUid()).setValue(updateUser);
                    Toast.makeText(InfoActivity.this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}
