package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.View.ChatActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.PurchaseOrder;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class SellProductAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<PurchaseOrder> orders;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private User user;

    public SellProductAdapter(Context context, int idLayout, List<PurchaseOrder> orders) {
        this.context = context;
        this.idLayout = idLayout;
        this.orders = orders;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView postImg;
        TextView title, status;
        Button btnOK, btnChat;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_sellp, null);
        viewHolder.postImg = (ImageView) convertView.findViewById(R.id.sp_img);
        viewHolder.title = (TextView) convertView.findViewById(R.id.sp_title);
        viewHolder.status = (TextView) convertView.findViewById(R.id.sp_tt);
        viewHolder.btnOK = (Button) convertView.findViewById(R.id.sp_sent);
        viewHolder.btnChat = (Button) convertView.findViewById(R.id.sp_chat);
        convertView.setTag(viewHolder);
        if (convertView == null) {

        } else {
            //viewHolder = (ViewHolder) convertView.getTag();
        }
        final PurchaseOrder order = orders.get(position);
        Log.d("Biin", order.getPo_title());
        viewHolder.title.setText(order.getPo_title());
        if (order.getPo_status() == 0) {
            viewHolder.status.setText("Đang chờ xác nhận");
        }
        if (order.getPo_status() == 1) {
            viewHolder.status.setText("Đang giao hàng");
        }
        if (order.getPo_status() == 2) {
            viewHolder.status.setText("Đã nhận hàng");
        }
        Picasso.get().load(order.getPo_img()).into(viewHolder.postImg);

        viewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser(order);
            }
        });

        viewHolder.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getPo_status() == 2) {
                    Toast.makeText(context,"Mặt hàng này đã được bán",Toast.LENGTH_SHORT).show();
                } else {
                    mData.child("Orders").child(order.getPo_id()).child("po_status").setValue(1);
                }
            }
        });
        return convertView;
    }

    private void getUser(PurchaseOrder temp) {
        mData.child("User").orderByKey().equalTo(temp.getPo_buyerid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Client User", (Serializable) user);
                    context.startActivity(intent);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editStatusPost(PurchaseOrder orderTemp) {

    }
}
