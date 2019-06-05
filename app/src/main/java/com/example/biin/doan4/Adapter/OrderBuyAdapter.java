package com.example.biin.doan4.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.View.ChatActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.View.CustomReport;
import com.example.biin.doan4.View.ProfileActivity;
import com.example.biin.doan4.model.Post;
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

public class OrderBuyAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<PurchaseOrder> orders;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private RadioGroup rg;
    private RadioButton r1;
    private RadioButton r2;
    private RadioButton r3;
    private RadioButton r4;
    private RadioButton r5;
    private Post post;
    private CustomReport report;
    private Bitmap bitmap;

    public OrderBuyAdapter(Context context, int idLayout, List<PurchaseOrder> orders) {
        this.context = context;
        this.idLayout = idLayout;
        this.orders = orders;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        report.setBitmap(bitmap);
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
        OrderBuyAdapter.ViewHolder viewHolder = new OrderBuyAdapter.ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(idLayout, null);
            viewHolder.postImg = (ImageView) convertView.findViewById(R.id.or_img);
            viewHolder.title = (TextView) convertView.findViewById(R.id.or_title);
            viewHolder.status = (TextView) convertView.findViewById(R.id.or_tt);
            viewHolder.btnOK = (Button) convertView.findViewById(R.id.or_receive);
            viewHolder.btnChat = (Button) convertView.findViewById(R.id.or_chat);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderBuyAdapter.ViewHolder) convertView.getTag();
        }

        final PurchaseOrder order = orders.get(position);
        viewHolder.title.setText(order.getPo_title());
        if (order.getPo_status() == 0) {
            viewHolder.status.setText("Đang chờ xác nhận");
        }
        if (order.getPo_status() == 1) {
            viewHolder.status.setText("Đang vận chuyển");
        }
        if (order.getPo_status() == 2) {
            viewHolder.status.setText("Đã nhận hàng");
        }
        Picasso.get().load(order.getPo_img()).into(viewHolder.postImg);
        viewHolder.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getPo_status() == 2) {
                    Toast.makeText(context, "Bạn đã nhận món hàng này", Toast.LENGTH_SHORT).show();
                } else {
                    if (order.getPo_status() == 0) {
                        Toast.makeText(context, "Bạn chưa thể nhận được hàng ở thời điểm hiện tại", Toast.LENGTH_SHORT).show();
                    } else {
                        order.setPo_status(2);
                        mData.child("Orders").child(order.getPo_id()).child("po_status").setValue(2);
                        mData.child("HidePosts").orderByKey().equalTo(order.getPo_productid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    post = dataSnapshot1.getValue(Post.class);
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        final Dialog builder = new Dialog(context);
                        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        builder.setContentView(R.layout.custom_dialog);
                        //final EditText input = new EditText(context);
                        rg = new RadioGroup(context);
                        r1 = new RadioButton(context);
                        r2 = new RadioButton(context);
                        r3 = new RadioButton(context);
                        r4 = new RadioButton(context);
                        r5 = new RadioButton(context);
                        Button ok = new Button(context);
                        Button rp = new Button(context);
                        report = new CustomReport((Activity) context, order.getPo_sellerid());
                        //input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ok = (Button) builder.findViewById(R.id.or_ok);
                        rp = (Button) builder.findViewById(R.id.or_rp);
                        rg = (RadioGroup) builder.findViewById(R.id.or_rg);
                        r1 = (RadioButton) builder.findViewById(R.id.or_1);
                        r2 = (RadioButton) builder.findViewById(R.id.or_2);
                        r3 = (RadioButton) builder.findViewById(R.id.or_3);
                        r4 = (RadioButton) builder.findViewById(R.id.or_4);
                        r5 = (RadioButton) builder.findViewById(R.id.or_5);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int idrb = rg.getCheckedRadioButtonId();
                                RadioButton radioButton = (RadioButton) builder.findViewById(idrb);
                                post.setPost_score(Integer.parseInt(radioButton.getText().toString()));
                                Log.d("Biin", String.valueOf(post.getPost_score()));
                                mData.child("HidePosts").child(order.getPo_productid()).child("post_score").setValue(post.getPost_score(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(context, "Đã đánh giá thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mData.child("Orders").child(order.getPo_id()).child("po_status").setValue(2);
                                builder.dismiss();
                            }
                        });
                        rp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                report.show();
                            }
                        });
                        builder.show();
                    }
                }
            }
        });

        viewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser(order);
            }
        });

        return convertView;
    }

    private void getUser(PurchaseOrder temp) {
        mData.child("User").orderByKey().equalTo(temp.getPo_sellerid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
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
}
