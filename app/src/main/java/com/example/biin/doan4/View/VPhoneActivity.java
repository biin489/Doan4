package com.example.biin.doan4.View;

import android.support.annotation.NonNull;
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
import com.example.biin.doan4.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VPhoneActivity extends AppCompatActivity {

    private User user;
    private String phone;

    private FirebaseAuth mAuth;
    DatabaseReference mData;

    private Button btnSend, btnVerify;
    private EditText edtPhone, edtCode;
    private ImageView ivBack;
    private TextView tvResend;
    private int isReceive;
    private String id;
    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vphone);

        user = (User) getIntent().getSerializableExtra("User");
        phone = getIntent().getStringExtra("Phone");

        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        init();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtPhone.getText().toString().equals("")) {
                    if (edtPhone.length() >= 10) {
                        String formatPhone = edtPhone.getText().toString();
                        if (formatPhone.charAt(0) == '0') {
                            temp = "+84";
                            for (int i = 1; i < formatPhone.length(); i++) {
                                temp += formatPhone.charAt(i);
                            }
                            Log.d("Biin", temp);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(temp, 60, TimeUnit.SECONDS, VPhoneActivity.this, mCallbacks);
                            Toast.makeText(VPhoneActivity.this, "Đã gửi mã", Toast.LENGTH_SHORT).show();
                        } else {
                            if (edtPhone.getText().toString().startsWith("+84")) {
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(edtPhone.getText().toString(), 60, TimeUnit.SECONDS, VPhoneActivity.this, mCallbacks);
                                Toast.makeText(VPhoneActivity.this, "Đã gửi mã", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(VPhoneActivity.this, "Hãy nhập số điện thoại đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VPhoneActivity.this, "Hãy nhập số điện thoại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(VPhoneActivity.this);
                if (!edtCode.getText().toString().equals("")) {
                    dialog.show();
                    AuthCredential credential = PhoneAuthProvider.getCredential(id, edtCode.getText().toString());
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mData.child("User").child(user.getUser_id()).child("user_isVerify").setValue(true);
                                mData.child("User").child(user.getUser_id()).child("user_phone").setValue(temp);
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(VPhoneActivity.this, "Hãy nhập mã gửi về điện thoại của bạn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        btnSend = findViewById(R.id.vphone_send);
        btnVerify = findViewById(R.id.vphone_verify);
        edtCode = findViewById(R.id.vphone_code);
        edtPhone = findViewById(R.id.vphone_phone);
        ivBack = findViewById(R.id.vphone_back);
        tvResend = findViewById(R.id.vphone_resend);
        edtPhone.setText(phone);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            id = verificationId;
        }
    };
}
