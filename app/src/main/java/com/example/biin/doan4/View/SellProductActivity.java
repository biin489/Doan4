package com.example.biin.doan4.View;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.biin.doan4.Adapter.SellProductAdapter;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.PurchaseOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SellProductActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    private ImageView ivBack;
    private ListView lv, lvdone;
    private SellProductAdapter adapter = null;
    private SellProductAdapter adapterdone = null;
    private ArrayList<PurchaseOrder> orders, ordersdone;

    private ArrayList<String> lsId, lsIdDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);

        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        init();
        getData();

        adapter = new SellProductAdapter(SellProductActivity.this, R.layout.item_sellp, orders);
        lv.setAdapter(adapter);
        adapterdone = new SellProductAdapter(SellProductActivity.this, R.layout.item_sellp, ordersdone);
        lvdone.setAdapter(adapterdone);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        ivBack = findViewById(R.id.sp_back);
        lv = findViewById(R.id.sp_lv);
        lvdone = findViewById(R.id.sp_lvdone);
        orders = new ArrayList<>();
        ordersdone = new ArrayList<>();
        lsId = new ArrayList<>();
        lsIdDone = new ArrayList<>();
    }

    private void getData() {
//        mData.child("Orders").orderByChild("po_sellerid").equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dn : dataSnapshot.getChildren()) {
//                    PurchaseOrder order = dn.getValue(PurchaseOrder.class);
//                    orders.add(order);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        mData.child("Orders").orderByChild("po_sellerid").equalTo(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Log.d("Biin", dataSnapshot.toString());
                PurchaseOrder order = dataSnapshot.getValue(PurchaseOrder.class);
                if (order.getPo_status() == 2) {
                    ordersdone.add(order);
                    lsIdDone.add(order.getPo_id());
                    adapterdone.notifyDataSetChanged();
                } else {
                    orders.add(order);
                    lsId.add(order.getPo_id());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PurchaseOrder order = dataSnapshot.getValue(PurchaseOrder.class);
                int index = lsId.indexOf(dataSnapshot.getKey());
                if (order.getPo_status() == 2) {
                    orders.remove(index);
                    lsId.remove(index);
                    ordersdone.add(order);
                    lsIdDone.add(order.getPo_id());
                    adapter.notifyDataSetChanged();
                    adapterdone.notifyDataSetChanged();
                } else {
                    orders.get(index).setPo_status(order.getPo_status());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PurchaseOrder order = dataSnapshot.getValue(PurchaseOrder.class);
                int index = lsId.indexOf(dataSnapshot.getKey());
                if (order.getPo_status() == 2) {
                    ordersdone.remove(index);
                    lsIdDone.remove(index);
                    adapterdone.notifyDataSetChanged();
                } else {
                    orders.remove(index);
                    lsId.remove(index);
                    adapter.notifyDataSetChanged();
                }
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
