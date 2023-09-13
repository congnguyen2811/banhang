package com.example.appbanhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.R;
import com.example.appbanhang.model.LoaiSP;

import java.util.List;

public class LoaiSpAdapter extends BaseAdapter {
    List<LoaiSP> list;
    Context context;

    public LoaiSpAdapter(Context context,List<LoaiSP> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public class ViewHolder{
        ImageView imgSP;
        TextView txtTenSP;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_item_sanpham,null);

            viewHolder.txtTenSP = convertView.findViewById(R.id.txtTenSP);
            viewHolder.imgSP = convertView.findViewById(R.id.item_img);
            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.txtTenSP.setText(list.get(position).getTensanpham());
        Glide.with(context).load(list.get(position).getHinhanh()).into(viewHolder.imgSP);
        return convertView;
    }
}
