package com.example.biin.doan4.View;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;

public class AllPostActivity extends AppCompatActivity {

    private String type;
    private ArrayList<Post> displayPost, lsDate1, lsDate2, lsPrice, lsStatus;
    private ArrayList<String> lsId;
    private SearchAdapter adapter = null;

    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    private ListView lvResult;
    private EditText edtPage;
    private ImageButton ibPrev, ibNext;
    private ImageView ivBack;
    private TextView tvTitle;
    private Spinner spSortby, spSorttype;
    private CustomDialog dialog;

    private int currentPage;
    private String[] strBy = {"Thời điểm đăng", "Giá", "Độ mới"};
    private String[] strType = {"Từ thấp đến cao", "Từ cao đến thấp"};
    private String[] strType2 = {"Từ cũ đến mới", "Từ mới đến cũ"};
    private int str1, str2;
    private boolean isFirstime, isInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_post);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        init();
        //loadData();
        getData();
        adapter = new SearchAdapter(AllPostActivity.this, displayPost, new ArrayList<User>(), 0);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lsDate1.size() == 0) {
                    Toast.makeText(AllPostActivity.this,"Không có sản phẩm nào",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    sortByDate();
                    sortByPrice12();
                    sortByStatus12();
                    currentPage = 1;
                    if (!setDataPaging(lsDate1)) {
                        Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                    } else {
                        edtPage.setText(String.valueOf(currentPage));
                        tvTitle.setText(displayPost.get(0).getPost_type());
                        lvResult.setAdapter(adapter);
                    }
                    edtPage.setText("1");
                    edtPage.setFocusable(false);
                    edtPage.setClickable(false);
                    dialog.dismiss();
                }
            }
        }, 2000);

        //event
        spSortby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        str1 = 1; // thoi gian
                        displayPost.clear();
                        setSpinnerType();
                        break;
                    case 1:
                        str1 = 2; // gia
                        displayPost.clear();
                        setSpinnerType();
                        break;
                    case 2:
                        str1 = 3; // do moi
                        displayPost.clear();
                        setSpinnerType();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSorttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (str1 == 1) {
                    switch (position) {
                        case 0:
                            str2 = 12; // cu den moi
                            if (setDataPaging(lsDate1)) {
                                edtPage.setText("1");
                            }
                            adapter.notifyDataSetChanged();
                            break;
                        case 1:
                            str2 = 21; // moi den cu
                            if (setDataPaging(lsDate2)) {
                                edtPage.setText("1");
                            }
                            adapter.notifyDataSetChanged();
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            str2 = 12;
                            displayPost.clear();
                            if (str1 == 2) {
                                sortByPrice12();
                                if (setDataPaging(lsPrice)) {
                                    edtPage.setText("1");
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                if (str1 == 3) {
                                    sortByStatus12();
                                    if (setDataPaging(lsStatus)) {
                                        edtPage.setText("1");
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case 1:
                            str2 = 21;
                            displayPost.clear();
                            if (str1 == 2) {
                                sortByPrice21();
                                if (setDataPaging(lsPrice)) {
                                    edtPage.setText("1");
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                if (str1 == 3) {
                                    sortByStatus21();
                                    if (setDataPaging(lsStatus)) {
                                        edtPage.setText("1");
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ibPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 1) {
                    currentPage--;
                    if (str1 == 1) {
                        if (str2 == 12) {
                            if (!setDataPaging(lsDate1)) {
                                currentPage--;
                            } else {
                                edtPage.setText(String.valueOf(currentPage));
                            }
                        } else {
                            if (!setDataPaging(lsDate2)) {
                                currentPage--;
                            } else {
                                edtPage.setText(String.valueOf(currentPage));
                            }
                        }
                    }
                    if (str1 == 2) {
                        if (!setDataPaging(lsPrice)) {
                            currentPage--;
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    }
                    if (str2 == 3) {
                        if (!setDataPaging(lsStatus)) {
                            currentPage--;
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    }
                } else {
                    Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                if (str1 == 1) {
                    if (str2 == 12) {
                        if (!setDataPaging(lsDate1)) {
                            currentPage--;
                            Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    } else {
                        if (!setDataPaging(lsDate2)) {
                            currentPage--;
                            Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    }
                }
                if (str1 == 2) {
                    if (!setDataPaging(lsPrice)) {
                        currentPage--;
                        Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                    } else {
                        edtPage.setText(String.valueOf(currentPage));
                    }
                }
                if (str2 == 3) {
                    if (!setDataPaging(lsStatus)) {
                        currentPage--;
                        Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                    } else {
                        edtPage.setText(String.valueOf(currentPage));
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

        edtPage.setImeActionLabel("Xong",KeyEvent.KEYCODE_ENTER);
        edtPage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.KEYCODE_ENTER || event.getAction() == KeyEvent.KEYCODE_SPACE) {
                    int page = Integer.parseInt(v.getText().toString().trim());
                    int temp = currentPage;
                    currentPage = page;
                    if (str1 == 1) {
                        if (str2 == 12) {
                            if (!setDataPaging(lsDate1)) {
                                currentPage = temp;
                                edtPage.setText(String.valueOf(currentPage));
                                Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                            } else {
                                edtPage.setText(String.valueOf(currentPage));
                            }
                        } else {
                            if (!setDataPaging(lsDate2)) {
                                currentPage = temp;
                                edtPage.setText(String.valueOf(currentPage));
                                Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                            } else {
                                edtPage.setText(String.valueOf(currentPage));
                            }
                        }
                    }
                    if (str1 == 2) {
                        if (!setDataPaging(lsPrice)) {
                            currentPage = temp;
                            edtPage.setText(String.valueOf(currentPage));
                            Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    }
                    if (str2 == 3) {
                        if (!setDataPaging(lsStatus)) {
                            currentPage = temp;
                            edtPage.setText(String.valueOf(currentPage));
                            Toast.makeText(AllPostActivity.this, "Không thể cuộn trang", Toast.LENGTH_SHORT).show();
                        } else {
                            edtPage.setText(String.valueOf(currentPage));
                        }
                    }
                }
                return false;
            }
        });
    }

    private void init() {
        type = getIntent().getStringExtra("Type");
        isInit = true;
        displayPost = new ArrayList<>();
        lsDate1 = new ArrayList<>();
        lsDate2 = new ArrayList<>();
        lsId = new ArrayList<>();
        lsPrice = new ArrayList<>();
        lsStatus = new ArrayList<>();
        lvResult = findViewById(R.id.ap_lv);
        edtPage = findViewById(R.id.ap_paging);
        ibNext = findViewById(R.id.ap_next);
        ibPrev = findViewById(R.id.ap_prev);
        ivBack = findViewById(R.id.ap_back);
        tvTitle = findViewById(R.id.ap_title);
        spSortby = findViewById(R.id.ap_sortby);
        spSorttype = findViewById(R.id.ap_sorttype);
        dialog = new CustomDialog(AllPostActivity.this);
        currentPage = 1;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AllPostActivity.this, android.R.layout.simple_spinner_item, strBy);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortby.setAdapter(arrayAdapter);
    }

    private void sortByDate() {
        Collections.reverse(lsDate2);
    }

    private void sortByPrice12() {
        lsPrice.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o1.getPost_price() < o2.getPost_price() ? -1 : o1.getPost_price() > o2.getPost_price() ? 1 : 0;
            }
        });
    }

    private void sortByStatus12() {
        lsStatus.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o1.getPost_status() < o2.getPost_status() ? -1 : o1.getPost_status() > o2.getPost_status() ? 1 : 0;
            }
        });
    }

    private void sortByPrice21() {
        lsPrice.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o1.getPost_price() > o2.getPost_price() ? -1 : o1.getPost_price() < o2.getPost_price() ? 1 : 0;
            }
        });
    }

    private void sortByStatus21() {
        lsStatus.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o1.getPost_status() > o2.getPost_status() ? -1 : o1.getPost_status() < o2.getPost_status() ? 1 : 0;
            }
        });
    }

    private void getData() {
        mData.child("Posts").orderByChild("post_type").equalTo(type).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                lsDate1.add(post);
                lsDate2.add(post);
                lsPrice.add(post);
                lsStatus.add(post);
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

    private void setSpinnerType() {
        if (str1 == 1) {
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(AllPostActivity.this, android.R.layout.simple_spinner_item, strType2);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSorttype.setAdapter(arrayAdapter2);
        } else {
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(AllPostActivity.this, android.R.layout.simple_spinner_item, strType);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSorttype.setAdapter(arrayAdapter2);
        }
    }

    private boolean setDataPaging(ArrayList<Post> posts) {
        if (currentPage * 10 < posts.size()) {
            displayPost.clear();
            for (int i = 9; i >= 0; i--) {
                displayPost.add(posts.get(currentPage * 10 - i - 1));
            }
            adapter.notifyDataSetChanged();
            return true;
        } else {
            if (currentPage * 10 - 10 < posts.size()) {
                displayPost.clear();
                for (int i = currentPage * 10 - 10; i < posts.size(); i++) {
                    displayPost.add(posts.get(i));
                }
                adapter.notifyDataSetChanged();
                return true;
            } else {
                return false;
            }
        }
    }

//    private void sortForDefaultValue() {
//        if (str1.equals("giá")) {
//            posts.sort(new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o1.getPost_price() < o2.getPost_price() ? -1 : o1.getPost_price() > o2.getPost_price() ? 1 : 0;
//                }
//            });
//            if (!setDataPaging()) {
//
//            } else {
//                edtPage.setText(String.valueOf(currentPage));
//                tvTitle.setText(displayPost.get(1).getPost_type());
//            }
//        } else {
//            if (str1.equals("thời gian")) {
//                posts.clear();
//                posts = tempPost;
//                if (!setDataPaging()) {
//
//                } else {
//                    edtPage.setText(String.valueOf(currentPage));
//                    tvTitle.setText(displayPost.get(1).getPost_type());
//                }
//            } else {
//                posts.sort(new Comparator<Post>() {
//                    @Override
//                    public int compare(Post o1, Post o2) {
//                        return o1.getPost_status() < o2.getPost_status() ? -1 : o1.getPost_status() > o2.getPost_status() ? 1 : 0;
//                    }
//                });
//                if (!setDataPaging()) {
//
//                } else {
//                    edtPage.setText(String.valueOf(currentPage));
//                    tvTitle.setText(displayPost.get(1).getPost_type());
//                }
//            }
//        }
//    }
//
//    private void sort() {
//        if (str1.equals("giá")) {
//            posts.sort(new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o1.getPost_price() < o2.getPost_price() ? -1 : o1.getPost_price() > o2.getPost_price() ? 1 : 0;
//                }
//            });
//            if (str2.equals("thấp đến cao")) {
//                if (!isFirstime) {
//                    Collections.reverse(posts);
//                    if (!setDataPaging()) {
//
//                    } else {
//                        edtPage.setText(String.valueOf(currentPage));
//                        tvTitle.setText(displayPost.get(1).getPost_type());
//                    }
//                }
//            } else {
//                isFirstime = false;
//                Collections.reverse(posts);
//                if (!setDataPaging()) {
//
//                } else {
//                    edtPage.setText(String.valueOf(currentPage));
//                    tvTitle.setText(displayPost.get(1).getPost_type());
//                }
//            }
//        } else {
//            if (str1.equals("thời gian")) {
//                if (str2.equals("cũ đến mới")) {
//                    if (!isFirstime) {
//                        Collections.reverse(posts);
//                        if (!setDataPaging()) {
//
//                        } else {
//                            edtPage.setText(String.valueOf(currentPage));
//                            tvTitle.setText(displayPost.get(1).getPost_type());
//                        }
//                    } else {
//                        posts.clear();
//                        posts = tempPost;
//                        if (!setDataPaging()) {
//
//                        } else {
//                            edtPage.setText(String.valueOf(currentPage));
//                            tvTitle.setText(displayPost.get(1).getPost_type());
//                        }
//                    }
//                } else {
//                    isFirstime = false;
//                    Collections.reverse(posts);
//                    if (!setDataPaging()) {
//
//                    } else {
//                        edtPage.setText(String.valueOf(currentPage));
//                        tvTitle.setText(displayPost.get(1).getPost_type());
//                    }
//                }
//            } else {
//                posts.sort(new Comparator<Post>() {
//                    @Override
//                    public int compare(Post o1, Post o2) {
//                        return o1.getPost_status() < o2.getPost_status() ? -1 : o1.getPost_status() > o2.getPost_status() ? 1 : 0;
//                    }
//                });
//                if (str2.equals("thấp đến cao")) {
//                    if (!isFirstime) {
//                        Collections.reverse(posts);
//                        if (!setDataPaging()) {
//
//                        } else {
//                            edtPage.setText(String.valueOf(currentPage));
//                            tvTitle.setText(displayPost.get(1).getPost_type());
//                        }
//                    }
//                } else {
//                    isFirstime = false;
//                    Collections.reverse(posts);
//                    if (!setDataPaging()) {
//
//                    } else {
//                        edtPage.setText(String.valueOf(currentPage));
//                        tvTitle.setText(displayPost.get(1).getPost_type());
//                    }
//                }
//            }
//        }
//    }
}
