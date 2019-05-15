package com.example.biin.doan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.biin.doan4.Adapter.CustomGridAdapter;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private GridView gridViewdt, gridViewgd, gridViewtt, gridViewvp, gridViewk;
    private ViewFlipper viewFlipper;
    private EditText edtFind;
    private ArrayList<Post> listpostdt, listpostdgd, listposttt, listpostdvp, listpostk, listpanel;
    private List<String> kl_dt, kl_dgd, kl_tt, kl_dvp, kl_k;
    private ImageView imgMenu, imgUser, imgAvatar;
    private Button btnLogin;
    private NavigationView navigationView;
    private TextView tvEmail, tvttDientu, tvttGiadung, tvttThoitrang, tvttVanphong, tvttKhac, xtDt, xtGd, xtTt, xtVp, xtK;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FloatingActionButton fabAdd;
    private CustomGridAdapter dt = null, vp = null, tt = null, gd = null, k = null;
    private User crUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        init();

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        getUser();
        ActionViewFlipper();

        //getdata
        dt = new CustomGridAdapter(this, R.layout.grid_item_layout, listpostdt);
        gridViewdt.setAdapter(dt);
        gd = new CustomGridAdapter(this, R.layout.grid_item_layout, listpostdgd);
        gridViewgd.setAdapter(gd);
        tt = new CustomGridAdapter(this, R.layout.grid_item_layout, listposttt);
        gridViewtt.setAdapter(tt);
        vp = new CustomGridAdapter(this, R.layout.grid_item_layout, listpostdvp);
        gridViewvp.setAdapter(vp);
        k = new CustomGridAdapter(this, R.layout.grid_item_layout, listpostk);
        gridViewk.setAdapter(k);
        getData();
        //ActionViewFlipper();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_logout:
                        if (mAuth.getCurrentUser()!=null) {
                            mAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            user = mAuth.getCurrentUser();
                            imgUser.setImageResource(R.drawable.userimg);
                            drawerLayout.closeDrawers();
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_inbox:
                        if (mAuth.getCurrentUser() != null) {
                            drawerLayout.closeDrawers();
                            Intent myIntent = new Intent(MainActivity.this, InboxActivity.class);
                            MainActivity.this.startActivity(myIntent);
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_myproduct:
                        if (mAuth.getCurrentUser() != null) {
                            drawerLayout.closeDrawers();
                            Toast.makeText(MainActivity.this, "Đang tải...", Toast.LENGTH_SHORT).show();
                            Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                            intentProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intentProfile.putExtra("User", (Serializable) crUser);
                            MainActivity.this.startActivity(intentProfile);
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_info:
                        if (mAuth.getCurrentUser() != null) {
                            drawerLayout.closeDrawers();
                            Intent myIntent2 = new Intent(MainActivity.this, InfoActivity.class);
                            MainActivity.this.startActivity(myIntent2);
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_qldhm:
                        if (mAuth.getCurrentUser() != null) {
                            drawerLayout.closeDrawers();
                            Intent myIntent1 = new Intent(MainActivity.this, MngOrderActivity.class);
                            MainActivity.this.startActivity(myIntent1);
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_qldhb:
                        if (mAuth.getCurrentUser() != null) {
                            drawerLayout.closeDrawers();
                            Intent myIntent3 = new Intent(MainActivity.this, SellProductActivity.class);
                            MainActivity.this.startActivity(myIntent3);
                        } else {
                            Toast.makeText(MainActivity.this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        }
                        break;
                }
                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                return true;
            }
        });

        //
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//                MainActivity.this.startActivity(myIntent);
//            }
//        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                tvEmail = drawerLayout.findViewById(R.id.nav_email);
                imgAvatar = findViewById(R.id.nav_img);
                if (mAuth.getCurrentUser() != null) {
                    tvEmail.setText(mAuth.getCurrentUser().getEmail());
                    Picasso.get().load(crUser.getUser_avatar()).transform(new CircleTransform()).into(imgAvatar);
                } else {
                    imgAvatar.setVisibility(View.GONE);
                    tvEmail.setText("Đăng nhập/Đăng ký");
                    tvEmail.setTextSize(18);
                    tvEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(myIntent);
                        }
                    });
                }
                //btnLogin1 = drawerLayout.findViewById(R.id.btn_login1);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent myIntent = new Intent(MainActivity.this, AddActivity.class);
                    MainActivity.this.startActivity(myIntent);
                } else {
                    showLoginDialog("Bạn cần đăng nhập để thực hiện chức năng này");
                }
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("User", crUser);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });

        edtFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, FindActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        edtFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Intent myIntent = new Intent(MainActivity.this, FindActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        tvttDientu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ điện tử");
                MainActivity.this.startActivity(intent);
            }
        });

        xtDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ điện tử");
                MainActivity.this.startActivity(intent);
            }
        });

        tvttVanphong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ văn phòng");
                MainActivity.this.startActivity(intent);
            }
        });

        xtVp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ văn phòng");
                MainActivity.this.startActivity(intent);
            }
        });

        tvttThoitrang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Thời trang");
                MainActivity.this.startActivity(intent);
            }
        });

        xtTt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Thời trang");
                MainActivity.this.startActivity(intent);
            }
        });

        tvttGiadung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ gia dụng");
                MainActivity.this.startActivity(intent);
            }
        });

        xtGd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Đồ gia dụng");
                MainActivity.this.startActivity(intent);
            }
        });

        tvttKhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Khác");
                MainActivity.this.startActivity(intent);
            }
        });

        xtK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Type", "Khác");
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        edtFind = findViewById(R.id.find_search);
        imgMenu = findViewById(R.id.find_menu);
        fabAdd = findViewById(R.id.btn_add);
        gridViewdt = findViewById(R.id.gr_dientu);
        gridViewgd = findViewById(R.id.gr_giadung);
        gridViewtt = findViewById(R.id.gr_thoitrang);
        gridViewvp = findViewById(R.id.gr_vanphong);
        gridViewk = findViewById(R.id.gr_khac);
        listpostdt = new ArrayList<>();
        listpostdgd = new ArrayList<>();
        listposttt = new ArrayList<>();
        listpostdvp = new ArrayList<>();
        listpostk = new ArrayList<>();
        kl_dt = new ArrayList<String>();
        kl_dgd = new ArrayList<String>();
        kl_tt = new ArrayList<String>();
        kl_dvp = new ArrayList<String>();
        kl_k = new ArrayList<String>();
        viewFlipper = findViewById(R.id.view_flipper);
        imgUser = findViewById(R.id.find_user);
        tvttDientu = findViewById(R.id.gr_titleDientu);
        tvttGiadung = findViewById(R.id.gr_titleGiadung);
        tvttThoitrang = findViewById(R.id.gr_titleThoitrang);
        tvttVanphong = findViewById(R.id.gr_titleVanphong);
        tvttKhac = findViewById(R.id.gr_titleKhac);
        xtDt = findViewById(R.id.gr_xemthem_dt);
        xtGd = findViewById(R.id.gr_xemthem_gd);
        xtK = findViewById(R.id.gr_xemthem_k);
        xtTt = findViewById(R.id.gr_xemthem_tt);
        xtVp = findViewById(R.id.gr_xemthem_vp);
    }

    private void getData() {
        mData.child("Posts").orderByChild("post_type").equalTo("Đồ điện tử").limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                kl_dt.add(dataSnapshot.getKey());
                listpostdt.add(p1);
                dt.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_dt.indexOf(dataSnapshot.getKey());
                listpostdt.remove(index);
                kl_dt.remove(index);
                dt.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("Posts").orderByChild("post_type").equalTo("Đồ gia dụng").limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                listpostdgd.add(p1);
                kl_dgd.add(dataSnapshot.getKey());
                gd.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_dgd.indexOf(dataSnapshot.getKey());
                listpostdgd.remove(index);
                kl_dgd.remove(index);
                gd.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("Posts").orderByChild("post_type").equalTo("Thời trang").limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                kl_tt.add(dataSnapshot.getKey());
                listposttt.add(p1);
                tt.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_tt.indexOf(dataSnapshot.getKey());
                listposttt.remove(index);
                kl_tt.remove(index);
                tt.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("Posts").orderByChild("post_type").equalTo("Đồ văn phòng").limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                listpostdvp.add(p1);
                kl_dvp.add(dataSnapshot.getKey());
                vp.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_dvp.indexOf(dataSnapshot.getKey());
                listpostdvp.remove(index);
                kl_dvp.remove(index);
                vp.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("Posts").orderByChild("post_type").equalTo("Khác").limitToLast(4).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post p1 = dataSnapshot.getValue(Post.class);
                listpostk.add(p1);
                kl_k.add(dataSnapshot.getKey());
                k.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = kl_k.indexOf(dataSnapshot.getKey());
                listpostk.remove(index);
                kl_k.remove(index);
                k.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ActionViewFlipper() {
        for (int i = 0; i < 4; i++) {
            ImageView img = new ImageView(getApplicationContext());
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/demodoan1.appspot.com/o/postImg%2Funtitled3.jpg?alt=media&token=b01d122d-584d-431e-80a0-a663e6f9eff4").into(img);
            viewFlipper.addView(img);
        }
        viewFlipper.setFlipInterval(5000);//Chạy trong 5s
        viewFlipper.setAutoStart(true);//cho view flipper tự chạy
        Animation animation_slide_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation animation_slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        viewFlipper.setInAnimation(animation_slide_in);
        viewFlipper.setOutAnimation(animation_slide_out);
    }

    private void getUser() {
        if (mAuth.getCurrentUser() != null) {
            mData.child("User").orderByChild("user_id").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        crUser = ds.getValue(User.class);
                        break;
                    }
                    Picasso.get().load(crUser.getUser_avatar()).transform(new CircleTransform()).into(imgUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

        }
    }

    @Override
    protected void onPostResume() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onPostResume();
    }

    private void showLoginDialog(String noti) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage(noti);
        builder.setCancelable(true);
        builder.setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
