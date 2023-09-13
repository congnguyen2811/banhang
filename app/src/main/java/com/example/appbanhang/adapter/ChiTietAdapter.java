package com.example.appbanhang.adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.R;
import com.example.appbanhang.model.Item;

import java.util.List;


public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.MyViewHolder> {
    Context context;
    List<Item> itemList;
    public ChiTietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_chitiet,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        for(int i = 0 ; i < itemList.size();i++){
            holder.item_txttensp_CT.setText(item.getTensp()+"");
            holder.item_txtSoluong_CT.setText("Số lượng: " + item.getSoluong());
            Glide.with(context).load(item.getHinhanh()).into(holder.item_img_CT);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_img_CT;
        TextView item_txttensp_CT,item_txtSoluong_CT;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_img_CT = itemView.findViewById(R.id.item_imgchitiet);
            item_txttensp_CT = itemView.findViewById(R.id.item_txttensp_CT);
            item_txtSoluong_CT = itemView.findViewById(R.id.item_txtSoluong_CT);
        }
    }
}
