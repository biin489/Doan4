package com.example.biin.doan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPw;
    private Button btnLogin;
    private TextView tvForgotPw, tvRegis;
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private String email = "";
    private ArrayList<String> permisson;
    private User user;
    private Intent myIntent, myIntent2;

    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    private CustomDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();

        //Quen mat khau
        tvForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        //Dang ky
        tvRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            email = object.getString("email");
                            Log.d("Biin", email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Biin", e.toString());
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d("Biin", "Cancel Login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Biin", "Error Login");
            }
        });
        checkLoginFacebook();
    }

    private void init() {
        edtEmail = findViewById(R.id.login_email);
        edtPw = findViewById(R.id.login_pw);
        btnLogin = findViewById(R.id.login_btn_login);
        tvForgotPw = findViewById(R.id.login_forgotpw);
        tvRegis = findViewById(R.id.login_regis);
        loginButton = findViewById(R.id.login_button);
        permisson = new ArrayList<>();
        permisson.add("public_profile");
        permisson.add("email");
        dialogLoading = new CustomDialog(LoginActivity.this);
        myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent2 = new Intent(LoginActivity.this, RegisActivity.class);
    }

    private void Login() {
        String email = edtEmail.getText().toString();
        String pw = edtPw.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        if (email.equals("")) {
            dialogLoading.dismiss();
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Thông báo");
            builder.setMessage("Tài khoản facebook của bạn chưa có email, bạn cần tạo mới tài khoản bằng email để có thể đăng nhập, tạo mới ngay?");
            builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.this.startActivity(myIntent2);
                }
            });
            builder.show();
        } else {
            mData.child("User").orderByChild("user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    Log.d("Biin", String.valueOf(dataSnapshot.getChildrenCount()));
                    if (dataSnapshot.getChildrenCount() == 0) {                                                 // kiem tra xem da co email dang ki chua
                        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    FirebaseAuthUserCollisionException exception =
                                            (FirebaseAuthUserCollisionException) task.getException();
                                } else {
                                    getInfoCreateAcc();
                                    dialogLoading.dismiss();
                                    LoginActivity.this.startActivity(myIntent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            user = ds.getValue(User.class);
                            break;
                        }
                        if (user.isUser_isLinked() == 2 || user.isUser_isLinked() == 1) {
                            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        FirebaseAuthUserCollisionException exception =
                                                (FirebaseAuthUserCollisionException) task.getException();
                                    } else {
                                        updateInfo();
                                        dialogLoading.dismiss();
                                        LoginActivity.this.startActivity(myIntent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            if (user.isUser_isLinked() == 0) {
                                dialogLoading.dismiss();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Thông báo");
                                builder.setMessage("Email liên kết với tài khoản này đã tồn tại, bạn có muốn liên kết tài khoản với email hiện có không?");
                                builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(LoginActivity.this, LinkAccActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("Email", user.getUser_email());
                                        intent.putExtra("Token", token);
                                        LoginActivity.this.startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkLoginFacebook() {
        //LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, permisson);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                JSONObject json = response.getJSONObject();
                                if (json != null) {
                                    try {
                                        email = json.getString("email");
                                        Log.d("Biin", email);
                                    } catch (Exception e) {
                                        Log.d("Biin", "Error email: "+ e.toString());
                                    }
                                }
                                dialogLoading.show();
                                handleFacebookAccessToken(loginResult.getAccessToken());
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
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
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
                                    Log.d("Biin", profilePicUrl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }

    private void getInfoCreateAcc() {
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
                                    user.setUser_avatar(profilePicUrl);
                                    Log.d("Biin", profilePicUrl);
                                    String name = data.getString("first_name") + " " + data.getString("last_name");
                                    user.setUser_fullname(name);
                                    Log.d("Biin", name);
                                    String email = data.getString("email");
                                    user.setUser_email(email);
                                    Log.d("Biin", email);
                                    user.setUser_id(mAuth.getCurrentUser().getUid());
                                    user.setUser_isLinked(2);
                                    mData.child("User").child(user.getUser_id()).setValue(user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }
}
