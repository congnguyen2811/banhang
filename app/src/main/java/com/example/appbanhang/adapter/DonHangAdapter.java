package com.example.appbanhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanhang.Interface.ItemClickDeleteListener;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.model.DonHang;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> donHangList;
    ItemClickDeleteListener itemClickDeleteListener;

    public DonHangAdapter(Context context, List<DonHang> donHangList, ItemClickDeleteListener itemClickDeleteListener) {
        this.context = context;
        this.donHangList = donHangList;
        this.itemClickDeleteListener = itemClickDeleteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_donhang,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang  = donHangList.get(position);
        holder.item_txtDonhang.setText("Đơn hàng : " + donHang.getId());
        holder.item_txtTrangthai.setText(Utils.statusOrder(donHang.getTrangthai()));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickDeleteListener.onClickDelete(donHang.getId());
                return false;
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                holder.item_rcv_CT.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        linearLayoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        // adapter chi tiet
        ChiTietAdapter adapter = new ChiTietAdapter(context,donHang.getItem());
        holder.item_rcv_CT.setLayoutManager(linearLayoutManager);
        holder.item_rcv_CT.setAdapter(adapter);
        holder.item_rcv_CT.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return donHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_txtDonhang,item_txtTrangthai;
        RecyclerView item_rcv_CT;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_rcv_CT  = itemView.findViewById(R.id.item_rcvchitietdonhang);
            item_txtDonhang = itemView.findViewById(R.id.item_txtDonhang);
            item_txtTrangthai = itemView.findViewById(R.id.item_txtTinhTrangDon);
        }
    }
}
