package com.example.biin.doan4.View;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.biin.doan4.Adapter.SearchAdapter;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FindActivity extends AppCompatActivity {

    private ImageView ivMenu, ivUser;
    private ImageButton ibSearch;
    private EditText edtFind;
    private Spinner spWhat, spType;
    private ListView lvResult;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private String[] strWhat = {"Sản phẩm", "Người dùng"};
    private String[] strType1 = {"Theo tiêu đề", "Theo hãng sản xuất", "Theo giá", "Theo độ mới"};
    private String[] strType2 = {"Theo họ tên", "Theo email", "Theo số điện thoại"};
    private String str1 = "", str2 = "";
    private String setWhat = "", setType = "", array = "";

    private ArrayList<Post> posts;
    private ArrayList<User> users;
    private ArrayList<String> lsIdPost;
    private ArrayList<String> lsIdUser;
    private SearchAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        init();

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        spWhat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        str1 = "Sản phẩm";
                        setWhat = "Posts";
                        setSpinnerType();
                        edtFind.setText("");
                        posts.clear();
                        users.clear();
                        adapter = new SearchAdapter(FindActivity.this, posts, users, 0);
                        lvResult.setAdapter(adapter);
                        break;
                    case 1:
                        str1 = "Người dùng";
                        setWhat = "User";
                        setSpinnerType();
                        edtFind.setText("");
                        posts.clear();
                        users.clear();
                        adapter = new SearchAdapter(FindActivity.this, posts, users, 1);
                        lvResult.setAdapter(adapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (str1.equals("Sản phẩm")) {
                    switch (position) {
                        case 0:
                            str2 = "Theo tiêu đề";
                            setType = "post_title";
                            break;
                        case 1:
                            str2 = "Theo hãng sản xuất";
                            setType = "post_maker";
                            break;
                        case 2:
                            str2 = "Theo giá";
                            setType = "post_price";
                            break;
                        case 3:
                            str2 = "Theo độ mới";
                            setType = "post_status";
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            str2 = "Theo họ tên";
                            setType = "user_fullname";
                            break;
                        case 1:
                            str2 = "Theo email";
                            setType = "user_email";
                            break;
                        case 2:
                            str2 = "Theo số điện thoại";
                            setType = "user_phone";
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtFind.getText().toString().length() < 3) {

                } else {
                    array = edtFind.getText().toString();
                    users.clear();
                    posts.clear();
                    adapter.notifyDataSetChanged();
                    search();
                }
            }
        });
    }

    private void init() {
        ivMenu = findViewById(R.id.find_menu);
        ivUser = findViewById(R.id.find_user);
        ibSearch = findViewById(R.id.find_find);
        edtFind = findViewById(R.id.find_search);
        spWhat = findViewById(R.id.find_what);
        spType = findViewById(R.id.find_type);
        lvResult = findViewById(R.id.find_lv);
        posts = new ArrayList<>();
        lsIdPost = new ArrayList<>();
        users = new ArrayList<>();
        lsIdUser = new ArrayList<>();

        edtFind.requestFocus();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FindActivity.this, android.R.layout.simple_spinner_item, strWhat);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWhat.setAdapter(arrayAdapter);
    }

    private void setSpinnerType() {
        if (str1.equals("Sản phẩm")) {
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(FindActivity.this, android.R.layout.simple_spinner_item, strType1);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spType.setAdapter(arrayAdapter2);
        } else {
            if (str1.equals("Người dùng")) {
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(FindActivity.this, android.R.layout.simple_spinner_item, strType2);
                arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spType.setAdapter(arrayAdapter2);
            }
        }
    }

    private void search() {
        if (setWhat.equals("Posts")) {
            mData.child(setWhat).orderByChild(setType).startAt(array).endAt(array + "\uf8ff").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Post temp = dataSnapshot.getValue(Post.class);
                    posts.add(temp);
                    lsIdPost.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                    Log.d("Biin",temp.getPost_title());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            if (setWhat.equals("User")) {
                mData.child(setWhat).orderByChild(setType).startAt(array).endAt(array + "\uf8ff").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User temp = dataSnapshot.getValue(User.class);
                        users.add(temp);
                        lsIdUser.add(dataSnapshot.getKey());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
