package com.example.biin.doan4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biin.doan4.DetailPostActivity;
import com.example.biin.doan4.R;
import com.example.biin.doan4.model.Post;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class CustomGridAdapter extends BaseAdapter {

    private List<Post> post;
    private Context context;
    private int idlayout;
    NumberFormat formatter = new DecimalFormat("#,###");

    public CustomGridAdapter(Context context, int idlayout, List<Post> post) {
        this.post = post;
        this.context = context;
        this.idlayout = idlayout;
    }

    @Override
    public int getCount() {
        return post.size();
    }

    @Override
    public Object getItem(int position) {
        return post.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView price;
        TextView tt, bh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(idlayout, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.gr_img);
            viewHolder.title = (TextView) convertView.findViewById(R.id.gr_title);
            viewHolder.price = (TextView) convertView.findViewById(R.id.gr_price);
            viewHolder.tt = (TextView) convertView.findViewById(R.id.gr_tt);
            viewHolder.bh = (TextView) convertView.findViewById(R.id.gr_bh);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Post post1 = post.get(position);
        viewHolder.title.setText(post1.getPost_title());
        viewHolder.price.setText(formatter.format(post1.getPost_price()) + " đ");
        viewHolder.tt.setText(String.valueOf(post1.getPost_status()) + "%");
        if (post1.getPost_is_guarantee()) {
            viewHolder.bh.setText("Còn bảo hành");
        } else {
            viewHolder.bh.setText("Đã hết bảo hành");
        }
        Picasso.get().load(post1.getPost_image()).into(viewHolder.imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MA", (Serializable) post1);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
