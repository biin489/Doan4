package com.example.biin.doan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.example.biin.doan4.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkAccActivity extends AppCompatActivity {

    private String Email;
    private AccessToken token;
    ImageView ivBack;
    private TextView tvEmail, tvFPass;
    private EditText edtPass;
    private Button btnLogin;
    private String email, name, phone;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;

    private int countLogin;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_acc);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        Email = getIntent().getStringExtra("Email");
        token = getIntent().getParcelableExtra("Token");
        Log.d("Biin",token.toString());

        init();
        updateUI();

        //event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countLogin > 5) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LinkAccActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn đã nhập sai mật khẩu quá nhiều lần, chúng tôi đã gửi email cập nhật mật khẩu cho bạn. Hãy kiểm tra email và đăng nhập lại sau");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            countLogin = 0;
                            resetPassword(Email);
                        }
                    });
                } else {
                    if (tvEmail.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Bạn phải nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    } else {
                        String email = tvEmail.getText().toString();
                        String pw = edtPass.getText().toString();
                        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(LinkAccActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    linkAccount();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    LinkAccActivity.this.startActivity(intent);
                                } else {
                                    Toast.makeText(LinkAccActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                                    countLogin++;
                                }
                            }
                        });
                    }
                }
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
        tvEmail = findViewById(R.id.la_mail);
        ivBack = findViewById(R.id.la_back);
        tvFPass = findViewById(R.id.la_forgotpass);
        edtPass = findViewById(R.id.la_pass);
        btnLogin = findViewById(R.id.la_login);
        countLogin = 0;
    }

    private void updateUI() {
        tvEmail.setText(Email);
    }

    private void resetPassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;
        auth.sendPasswordResetEmail(emailAddress);
    }

    private void linkAccount() {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Log.d("Biin","Linked ok");
                        } else {

                        }
                    }
                });
        mData.child("User").child(mAuth.getCurrentUser().getUid()).child("user_isLinked").setValue(true);
        updateInfo();
    }

    private void checkLoginFacebook() {
        //LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, permisson);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        Log.d("Biin", "Success");
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("Biin", "Request");
                                JSONObject json = response.getJSONObject();
                                if (json.has("picture")) {
                                    try {
                                        String url = json.getJSONObject("picture").getJSONObject("data").getString("url");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    if (json != null) {
                                        email = json.getString("email");
                                        Log.d("Biin", email);
                                    }
                                } catch (JSONException e) {

                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "last_name,first_name,email,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    private void updateInfo() {
        Bundle params = new Bundle();
        params.putString("fields", "id,email,first_name,last_name,gender,picture.type(large)");
        new GraphRequest(token, "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    mData.child("User").child(mAuth.getCurrentUser().getUid()).child("user_avatar").setValue(profilePicUrl);
                                    String name = data.getString("first_name") + " " + data.getString("last_name");
                                    mData.child("User").child(mAuth.getCurrentUser().getUid()).child("user_fullname").setValue(name);
                                    String gender = data.getString("gender");
                                    mData.child("User").child(mAuth.getCurrentUser().getUid()).child("user_gender").setValue(gender);
                                    mData.child("User").child(mAuth.getCurrentUser().getUid()).child("user_isLinked").setValue(1);
                                    Log.d("Biin", profilePicUrl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }
}
