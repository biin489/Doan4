package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.AddActivity;
import com.example.biin.doan4.CustomDialog;
import com.example.biin.doan4.DetailPostActivity;
import com.example.biin.doan4.LoginActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class PfPostAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<Post> posts;
    int type;
    NumberFormat formatter = new DecimalFormat("#,###");
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    public PfPostAdapter(Context context, int idLayout, List<Post> posts, int type) {
        this.context = context;
        this.idLayout = idLayout;
        this.posts = posts;
        this.type = type;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView ivPostimg;
        TextView tvTitle, tvPrice, tvBh, tvPoint, tvTt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder = new ViewHolder();
        if (type == 0) {
            //init
            convertView = inflater.inflate(R.layout.item_pf_sell, null);
            viewHolder.ivPostimg = (ImageView) convertView.findViewById(R.id.pfsell_img);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.pfsell_title);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.pfsell_gia);
            viewHolder.tvBh = (TextView) convertView.findViewById(R.id.pfsell_bh);
            viewHolder.tvTt = (TextView) convertView.findViewById(R.id.pfsell_tt);
            convertView.setTag(viewHolder);
            //set value
            final Post post = posts.get(position);
            viewHolder.tvTitle.setText(post.getPost_title());
            viewHolder.tvPrice.setText(formatter.format(post.getPost_price()) + "đ");
            viewHolder.tvTt.setText(post.getPost_status() + "%");
            if (post.getPost_is_guarantee()) {
                viewHolder.tvBh.setText("Còn bảo hành");
            } else {
                viewHolder.tvBh.setText("Đã hết bảo hành");
            }
            Picasso.get().load(post.getPost_image()).resize(100, 100).into(viewHolder.ivPostimg);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailPostActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MA", (Serializable) post);
                    context.startActivity(intent);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn muốn thực hiện hành động nào với sản phẩm này?");
                    builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, AddActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Update", (Serializable) post);
                            context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mData.child("Posts").child(post.getPost_id()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(context,"Đã xóa sản phẩm",Toast.LENGTH_SHORT).show();
                                }
                            });
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
            //convertView.setOn
        } else {
            //init
            convertView = inflater.inflate(R.layout.item_pf_selled, null);
            viewHolder.ivPostimg = (ImageView) convertView.findViewById(R.id.pfselled_img);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.pfselled_title);
            viewHolder.tvPoint = (TextView) convertView.findViewById(R.id.pfselled_point);
            //set value
            Post post = posts.get(position);
            viewHolder.tvTitle.setText(post.getPost_title());
            viewHolder.tvPoint.setText(String.valueOf(post.getPost_score()));
            Picasso.get().load(post.getPost_image()).resize(100, 100).into(viewHolder.ivPostimg);
        }
        return convertView;
    }

}
