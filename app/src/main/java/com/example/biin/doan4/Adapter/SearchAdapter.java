package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biin.doan4.DetailPostActivity;
import com.example.biin.doan4.ProfileActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.example.biin.doan4.model.User;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    Context context;
    int idLayout;
    List<Post> posts;
    List<User> users;
    int type;
    NumberFormat formatter = new DecimalFormat("#,###");

    public SearchAdapter(Context context, List<Post> posts, List<User> users, int type) {
        this.context = context;
        this.posts = posts;
        this.users = users;
        this.type = type;
    }

    @Override
    public int getCount() {
        if (posts.size() == 0) {
            return users.size();
        } else {
            return posts.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (posts.size() == 0) {
            return users.get(position);
        } else {
            return posts.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView ivImg;
        TextView tvTitle, tvPrice, tvBh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder = new ViewHolder();
        //init
        convertView = inflater.inflate(R.layout.item_pf_sell, null);
        viewHolder.ivImg = (ImageView) convertView.findViewById(R.id.pfsell_img);
        viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.pfsell_title);
        viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.pfsell_gia);
        viewHolder.tvBh = (TextView) convertView.findViewById(R.id.pfsell_bh);
        convertView.setTag(viewHolder);
        if (type == 0) {
            //set value
            final Post post = posts.get(position);
            viewHolder.tvTitle.setText(post.getPost_title());
            viewHolder.tvPrice.setText(formatter.format(post.getPost_price()) + " đ");
            if (post.getPost_is_guarantee()) {
                viewHolder.tvBh.setText("Còn bảo hành");
            } else {
                viewHolder.tvBh.setText("Đã hết bảo hành");
            }
            Picasso.get().load(post.getPost_image()).resize(100, 100).into(viewHolder.ivImg);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailPostActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MA", (Serializable) post);
                    context.startActivity(intent);
                }
            });
        } else {
            //set value
            final User user = users.get(position);
            viewHolder.tvTitle.setText(user.getUser_fullname());
            if (user.getUser_age() == 0) {
                viewHolder.tvPrice.setText("Tuổi chưa rõ");
            } else {
                viewHolder.tvPrice.setText(String.valueOf(user.getUser_age()) + " tuổi");
            }
            if (user.getUser_gender().equals("")) {
                viewHolder.tvBh.setText("Giới tính chưa rõ");
            } else {
                viewHolder.tvBh.setText("Giới tính: " + user.getUser_gender());
            }
            if (user.getUser_avatar().equals("")) {
                viewHolder.ivImg.setImageResource(R.drawable.userimg);
            } else {
                Picasso.get().load(user.getUser_avatar()).resize(100, 100).into(viewHolder.ivImg);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("User", (Serializable) user);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

//    private String formatPrice(String price) {
//        String format = "";
//        for (int i = price.length(); i >= 0; i--) {
//            if (i != price.length()) {
//                if (((i + 1) % 3) == 0) {
//                    format += price.charAt(i) + ",";
//                } else {
//                    format += price.charAt(i);
//                }
//            } else {
//                format += price.charAt(i);
//            }
//        }
//        return format;
//    }
}
