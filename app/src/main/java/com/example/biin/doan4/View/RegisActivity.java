package com.example.biin.doan4.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.R;
import com.example.biin.doan4.model.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class RegisActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    private EditText edtUsername, edtEmail, edtPw, edtRepw, edtPhone;
    private Button btnSignup;
    private TextView tvLogin;

    private User user1;
    private String user;
    private String email;
    private String password;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        init();

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtUsername.getText().toString().equals("") || edtEmail.getText().toString().equals("") || edtPw.getText().toString().equals("") || edtRepw.getText().toString().equals("") || edtPhone.getText().toString().equals("")) {
                    Toast.makeText(RegisActivity.this, "Bạn phải nhập đủ các trường", Toast.LENGTH_SHORT).show();
                } else {
                    if (edtUsername.getText().toString().length() < 4) {
                        Toast.makeText(RegisActivity.this, "Tên quá ngắn", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edtPw.getText().toString().length() > 8) {
                            if (edtPw.getText().toString().equals(edtRepw.getText().toString())) {
                                Signup();
                            } else {
                                Toast.makeText(RegisActivity.this, "Mật khẩu bạn nhập không khớp!", Toast.LENGTH_SHORT).show();
                                edtPw.setText("");
                                edtRepw.setText("");
                            }
                        } else {
                            Toast.makeText(RegisActivity.this, "Mật khẩu phải lớn hơn 8 ký tự", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void init() {
        edtUsername = findViewById(R.id.regis_username);
        edtEmail = findViewById(R.id.regis_email);
        edtPw = findViewById(R.id.regis_pw);
        edtRepw = findViewById(R.id.regis_repw);
        edtPhone = findViewById(R.id.regis_phonenum);
        btnSignup = findViewById(R.id.regis_btn_login);
        tvLogin = findViewById(R.id.regis_login);
    }

    private void Signup() {
        final AccessToken token = AccessToken.getCurrentAccessToken();
        user = edtUsername.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPw.getText().toString();
        phone = edtPhone.getText().toString();
        if (token != null) {
            mData.child("User").orderByChild("user_phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    addUserToDatabase();
                                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Toast.makeText(RegisActivity.this, "Thành công, bạn sẽ được chuyển hướng trong giây lát", Toast.LENGTH_SHORT).show();
                                            Intent myIntent = new Intent(RegisActivity.this, MainActivity.class);
                                            RegisActivity.this.startActivity(myIntent);
                                        }
                                    });
                                } else {
                                    showAlertDialog("Email này đã được sử dụng, hãy thử đăng nhập hoặc dùng email khác");
                                }
                            }
                        });
                    } else {
                        showAlertDialog("Số điện thoại này đã được sử dụng, vui lòng chọn số điện thoại khác");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            mData.child("User").orderByChild("user_phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user1 = new User();
                                    user1.setUser_id(mAuth.getCurrentUser().getUid());
                                    user1.setUser_email(mAuth.getCurrentUser().getEmail());
                                    user1.setUser_name(user);
                                    user1.setUser_phone(phone);
                                    mData.child("User").child(user1.getUser_id()).setValue(user1);
                                    Toast.makeText(RegisActivity.this,"Thành công, bạn sẽ được chuyển hướng trong giây lát",Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(RegisActivity.this, MainActivity.class);
                                    RegisActivity.this.startActivity(myIntent);
                                } else {
                                    showAlertDialog("Email này đã được sử dụng, hãy thử đăng nhập hoặc dùng email khác");
                                }
                            }
                        });
                    } else {
                        showAlertDialog("Số điện thoại này đã được sử dụng, vui lòng chọn số điện thoại khác");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkPhone() {

    }

    private void showAlertDialog(String noti) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage(noti);
        builder.setCancelable(true);
        builder.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addUserToDatabase() {
        user1 = new User();
        getAvatar();
        user1.setUser_id(mAuth.getCurrentUser().getUid());
        user1.setUser_email(mAuth.getCurrentUser().getEmail());
        user1.setUser_name(user);
        user1.setUser_phone(phone);
        user1.setUser_isLinked(1);
        mData.child("User").orderByChild("user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    mData.child("User").orderByChild("user_phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                mData.child("User").child(user1.getUser_id()).setValue(user1);
                            } else {
                                showAlertDialog("Số điện thoại này đã được sử dụng, vui lòng chọn số điện thoại khác");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    showAlertDialog("Email này đã được sử dụng, vui lòng chọn email khác");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAvatar() {
        final User user = new User();
        Bundle params = new Bundle();
        params.putString("fields", "id,email,first_name,last_name,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    user1.setUser_avatar(profilePicUrl);
                                }
                                user1.setUser_fullname(data.getString("first_name") + " " + data.getString("last_name"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }

}
